package com.example.bikesoftware.presentation.speedClock

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikesoftware.ui.theme.DirtWhite
import com.example.bikesoftware.ui.theme.Grey
import com.example.bikesoftware.ui.theme.Orange

data class ScaleStyle(
    val scaleWidth: Dp = 140.dp,
    val radius: Dp = 400.dp,
    val normalLineColor: Color = DirtWhite,
    val tenStepLineColor: Color = DirtWhite,
    val scaleColor: Color = Grey,
    val normalLineLength: Dp = 15.dp,
    val fiveStepLineLength: Dp = 30.dp,
    val tenStepLineLength: Dp = 45.dp,
    val colorAccent: Color = Orange,
    val scaleIndicatorLength: Dp = 70.dp,
    val textSize: TextUnit = 18.sp,
    val textColor: Color = DirtWhite
)
