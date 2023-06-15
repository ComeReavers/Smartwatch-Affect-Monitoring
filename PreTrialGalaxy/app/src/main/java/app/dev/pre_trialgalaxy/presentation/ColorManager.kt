package app.dev.pre_trialgalaxy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults

class ColorManager {

    /*
    The colors used to signal which quadrant a button belongs to
     */
    val green = 0xFF56CB47
    val red = 0xFFC31E1E
    val yellow = 0xFFFFFB00
    val blue = 0xFF3E79E7

    // HA-HV
    fun getPositiveArousalCircularColor(): Color {
        return Color(green)
    }

    // LA-HV
    fun getNegativeArousalCircularColor(): Color {
        return Color(red)
    }

    // LA-LV
    fun getActiveValenceCircularColor(): Color {
        return Color(yellow)
    }

    // HA-LV
    fun getPassiveValenceCircularColor(): Color {
        return Color(blue)
    }

    /*
    The following functions are used to color a Composable Chip.
     */
    @Composable
    fun getPositiveChipColor(): ChipColors {
        return ChipDefaults.primaryChipColors(backgroundColor = getPositiveArousalCircularColor())
    }

    @Composable
    fun getNegativeChipColor(): ChipColors {
        return ChipDefaults.primaryChipColors(backgroundColor = getNegativeArousalCircularColor())
    }

    @Composable
    fun getActiveChipColor(): ChipColors {
        return ChipDefaults.primaryChipColors(backgroundColor = getActiveValenceCircularColor())
    }

    @Composable
    fun getPassiveChipColor(): ChipColors {
        return ChipDefaults.primaryChipColors(backgroundColor = getPassiveValenceCircularColor())
    }
}