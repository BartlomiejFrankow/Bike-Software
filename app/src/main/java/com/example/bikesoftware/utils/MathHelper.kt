package com.example.bikesoftware.utils

import kotlin.math.PI

private const val ROTATION = 180f

fun Float.toRadians() = this * (PI / ROTATION).toFloat()

fun Float.toDegrees() = this * (ROTATION / PI.toFloat())

fun Int.isFifthStep() = this % 5 == 0

fun Int.isTenStep() = this % 10 == 0
