package app.gamenative.ui.screen.wow

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FitScreenSizeTest {

    @Test
    fun sixteenByNineWindow_keepsBaseSize() {
        assertEquals("1920x1080", fitScreenSize("1920x1080", 2400, 1350))
    }

    @Test
    fun foldInnerScreen_keepsPixelBudget() {
        assertEquals("1104x834", fitScreenSize("1280x720", 2448, 1848))
    }

    @Test
    fun portraitWindow_usesLandscapeAspect() {
        assertEquals("1104x834", fitScreenSize("1280x720", 1848, 2448))
    }

    @Test
    fun adreno740Base_onFoldInnerScreen() {
        assertEquals("1658x1252", fitScreenSize("1920x1080", 2448, 1848))
    }

    @Test
    fun widePhone_widensAndShortens() {
        assertEquals("2146x966", fitScreenSize("1920x1080", 2400, 1080))
    }

    @Test
    fun adreno740Base_onRetroidNova_capsAtPanel() {
        assertEquals("1280x960", fitScreenSize("1920x1080", 1280, 960))
    }

    @Test
    fun maliBase_onRg477v_usesPanel() {
        assertEquals("1280x960", fitScreenSize("1280x960", 1280, 960))
    }

    @Test
    fun adreno8xxBase_onSmallFourByThree_staysUnderPanel() {
        assertEquals("1108x832", fitScreenSize("1280x720", 1280, 960))
    }

    @Test
    fun unknownWindow_keepsBaseSize() {
        assertEquals("1280x720", fitScreenSize("1280x720", 0, 0))
    }

    @Test
    fun fitsScreen_acceptsSameOrSmallerResolution() {
        assertTrue(fitsScreen("1104x834", "1104x834"))
        assertTrue(fitsScreen("800x600", "1104x834"))
    }

    @Test
    fun fitsScreen_rejectsLargerOrInvalidResolution() {
        assertFalse(fitsScreen("1920x1080", "1104x834"))
        assertFalse(fitsScreen("1206x764", "1104x834"))
        assertFalse(fitsScreen("auto", "1104x834"))
    }
}
