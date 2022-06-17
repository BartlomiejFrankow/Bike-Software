package com.example.bikesoftware.presentation.speedClock

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.withRotation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bikesoftware.R
import com.example.bikesoftware.presentation.speedClock.LineType.*
import com.example.bikesoftware.utils.isFifthStep
import com.example.bikesoftware.utils.isTenStep
import com.example.bikesoftware.utils.toDegrees
import com.example.bikesoftware.utils.toRadians
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

const val SHADOW_ALPHA = 255
const val SHADOW_RADIUS = 800f
private const val NUMBER_SPACE = 5
private const val NINETY_DEGREES_FLIP = 90
private const val INDICATOR_SIDE_WIDTH = 4f
private const val SCALE_START_VALUE = 0
private const val SCALE_END_VALUE = 50
private const val ANIMATION_DURATION = 1000

@Composable
fun SpeedClockScreen(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    viewModel: SpeedClockViewModel = hiltViewModel()
) {
    var center by remember {
        mutableStateOf(Offset.Zero)
    }
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    val animatedSpeed by animateFloatAsState(
        targetValue = viewModel.currentSpeed.value.toFloat(),
        animationSpec = tween(ANIMATION_DURATION)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = Color.White, fontSize = dimensionResource(R.dimen.large_text).value.sp)
                ) {
                    append(viewModel.currentSpeed.value.toString())
                }
                withStyle(
                    style = SpanStyle(color = Color.Green, fontSize = dimensionResource(R.dimen.big_text).value.sp)
                ) {
                    append(stringResource(R.string.kilometers_per_hour))
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = dimensionResource(R.dimen.speed_padding_bottom)),
            fontFamily = FontFamily(Font(R.font.lexend_thin)),
            fontWeight = FontWeight.Bold
        )

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.speed_clock_height))
                .align(Alignment.BottomCenter)
        ) {
            center = this.center
            circleCenter = Offset(
                x = center.x,
                y = (style.scaleWidth.toPx() / 2f) + style.radius.toPx()
            )
            val outerRadius = style.radius.toPx() + (style.scaleWidth.toPx() / 2f)
            val innerRadius = style.radius.toPx() - (style.scaleWidth.toPx() / 2f)

            drawScale(circleCenter, style)

            for (currentWeight in SCALE_START_VALUE..SCALE_END_VALUE) {
                val angleInRadians = (currentWeight - animatedSpeed - NINETY_DEGREES_FLIP).toRadians()
                val lineType = getLineType(currentWeight)
                val lineLength = getLineLength(lineType, style).toPx()

                drawLines(lineType, style, outerRadius, angleInRadians, circleCenter, lineLength)

                drawWeightValue(lineType, outerRadius, lineLength, style, angleInRadians, circleCenter, currentWeight)

                drawWeightIndicator(circleCenter, innerRadius, style)
            }

        } // the end of compose canvas

    } // the end of box

}

private fun DrawScope.drawWeightIndicator(
    circleCenter: Offset,
    innerRadius: Float,
    style: ScaleStyle
) {
    val middleTop = Offset(
        x = circleCenter.x,
        y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
    )
    val bottomLeft = Offset(
        x = circleCenter.x - INDICATOR_SIDE_WIDTH,
        y = circleCenter.y - innerRadius
    )
    val bottomRight = Offset(
        x = circleCenter.x + INDICATOR_SIDE_WIDTH,
        y = circleCenter.y - innerRadius
    )
    val indicator = Path().apply {
        moveTo(middleTop.x, middleTop.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        lineTo(bottomRight.x, bottomRight.y)
        lineTo(middleTop.x, middleTop.y)
    }

    drawPath(
        path = indicator,
        color = style.scaleIndicatorColor
    )
}

private fun DrawScope.drawLines(
    lineType: LineType,
    style: ScaleStyle,
    outerRadius: Float,
    angleInRadians: Float,
    circleCenter: Offset,
    lineLength: Float
) {
    val lineColor = getLineColor(lineType, style)
    val lineTop = Offset(
        x = outerRadius * cos(angleInRadians) + circleCenter.x,
        y = outerRadius * sin(angleInRadians) + circleCenter.y
    )
    val lineBottom = Offset(
        x = (outerRadius - lineLength) * cos(angleInRadians) + circleCenter.x,
        y = (outerRadius - lineLength) * sin(angleInRadians) + circleCenter.y
    )

    drawLine(
        color = lineColor,
        start = lineTop,
        end = lineBottom,
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawWeightValue(
    lineType: LineType,
    outerRadius: Float,
    lineLength: Float,
    style: ScaleStyle,
    angleInRadians: Float,
    circleCenter: Offset,
    currentWeight: Int
) {
    drawContext.canvas.nativeCanvas.apply {
        if (lineType is TenStep) {
            val textRadius = outerRadius - lineLength - NUMBER_SPACE.dp.toPx() - style.textSize.toPx()
            val x = textRadius * cos(angleInRadians) + circleCenter.x
            val y = textRadius * sin(angleInRadians) + circleCenter.y

            withRotation(
                degrees = angleInRadians.toDegrees() + NINETY_DEGREES_FLIP.toFloat(),
                pivotX = x,
                pivotY = y
            ) {
                drawText(
                    abs(currentWeight).toString(),
                    x,
                    y,
                    Paint().apply {
                        textSize = style.textSize.toPx()
                        textAlign = Paint.Align.CENTER
                    },
                )
            }
        }
    }
}

private fun DrawScope.drawScale(circleCenter: Offset, style: ScaleStyle) {
    drawContext.canvas.nativeCanvas.apply {
        drawCircle(
            circleCenter.x,
            circleCenter.y,
            style.radius.toPx(),
            Paint().apply {
                strokeWidth = style.scaleWidth.toPx()
                color = Color.White.hashCode()
                setStyle(Paint.Style.STROKE)
                setShadowLayer(SHADOW_RADIUS, 0f, 0f, android.graphics.Color.argb(SHADOW_ALPHA, 0, 0, 0))
            }
        )
    }
}

private fun getLineType(currentWeight: Int) = when {
    currentWeight.isTenStep() -> TenStep
    currentWeight.isFifthStep() -> FiveStep
    else -> NormalStep
}

private fun getLineLength(lineType: LineType, style: ScaleStyle) = when (lineType) {
    FiveStep -> style.fiveStepLineLength
    NormalStep -> style.normalLineLength
    TenStep -> style.tenStepLineLength
}

private fun getLineColor(lineType: LineType, style: ScaleStyle) = when (lineType) {
    FiveStep -> style.fiveStepLineColor
    NormalStep -> style.normalLineColor
    TenStep -> style.tenStepLineColor
}
