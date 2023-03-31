package app.dev.pre_trialgalaxy.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults

class ColorManager {
    
    val green = 0xFF56CB47
    val red = 0xFFC31E1E
    val yellow = 0xFFFFFB00
    val blue = 0xFF3E79E7


    fun getPositiveArousalCircularColor(): Color {
        return Color(green)
    }

    fun getNegativeArousalCircularColor(): Color {
        return Color(red)
    }

    fun getActiveValenceCircularColor(): Color {
        return Color(yellow)
    }

    fun getPassiveValenceCircularColor(): Color {
        return Color(blue)
    }

    
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


    @Composable
    fun getPositiveArousalButtonColor(): ButtonColors {
        return ButtonDefaults.primaryButtonColors(backgroundColor = getPositiveArousalCircularColor())
    }

    @Composable
    fun getPassiveValenceButtonColor(): ButtonColors {
        return ButtonDefaults.primaryButtonColors(backgroundColor = getPassiveValenceCircularColor())
    }
}