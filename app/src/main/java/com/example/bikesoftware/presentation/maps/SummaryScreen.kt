package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bikesoftware.R
import com.example.bikesoftware.ui.theme.TransparentBlack

@Composable
fun SummaryScreen(viewModel: MapViewModel, speeds: List<Int>) {

    fun getAverageSpeed() = if (speeds.isNotEmpty()) {
        (speeds.sum() / speeds.size).toString()
    } else {
        "0"
    }

    Box {
        Card(
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .align(Alignment.Center),
            backgroundColor = TransparentBlack,
            shape = RoundedCornerShape(8.dp),
            elevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = stringResource(R.string.trip_summary),
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    text = stringResource(R.string.trip_time, viewModel.getTripTimeSummary()),
                    color = Color.White
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp),
                    text = stringResource(R.string.average_speed, getAverageSpeed()),
                    color = Color.White
                )
            }
        }
    }
}
