# DXVK for Mali

`dxvk-2.4.1-wow-aarch64-mali.wcp` is DXVK 2.4.1 (doitsujin/dxvk commit `0cf05780`, the same source as
the Adreno build) built for Windows ARM64 with `dxvk-2.4.1-mali-wrapper-bcn.patch` applied. The Mali
profile uses it; the Adreno profiles keep `dxvk-2.4.1-wow-aarch64-test`.

## Why it exists

Mali GPUs have no BCn texture formats, and WoW uses them everywhere. GameNative's Vulkan wrapper
emulates BCn, but only inside `vkGetPhysicalDeviceFormatProperties` and `vkCmdCopyBufferToImage`.
DXVK 2.x calls the newer `vkGetPhysicalDeviceFormatProperties2` and `vkCmdCopyBufferToImage2`, which
the wrapper passes straight to the Mali driver. On the RG477V that meant:

- BC2 textures reported as unsampleable, so DXVK refused their views and WoW recreated its device in
  a loop.
- Compressed texture data uploaded undecoded, so every texture showed as static.

The patch makes DXVK merge in the legacy format query for BCn formats, and send buffer-to-image copies
through the legacy command whenever they carry no extension structs.

## Rebuilding

Built on Windows with:

- llvm-mingw 20241015 (ucrt, x86_64 host) from github.com/mstorsjo/llvm-mingw, `bin` on `PATH`
- meson and ninja from pip
- `glslangValidator` from the Vulkan SDK, on `PATH`

```bash
git clone https://github.com/doitsujin/dxvk.git && cd dxvk
git checkout 0cf05780abd7250c2cd713b7749cf32180157cf5
git submodule update --init --recursive
git apply ../tools/dxvk-mali/dxvk-2.4.1-mali-wrapper-bcn.patch
meson setup build-arm64 --cross-file ../tools/dxvk-mali/build-winarm64.txt --buildtype release \
  --prefix "$PWD/out" --strip -Denable_d3d9=false -Denable_d3d8=false -Denable_d3d10=false
ninja -C build-arm64 install
```

Package `out/bin/d3d11.dll` and `out/bin/dxgi.dll` as `system32/` in a zstd-compressed tar, with
`profile.json` (`versionName` `2.4.1-wow-aarch64-mali`) and the `licenses/` folder from the Adreno
package, then update its hash in `tools/components.sha256`.
