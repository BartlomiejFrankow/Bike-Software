package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bikesoftware.R
import com.example.bikesoftware.presentation.maps.Altitude.HIGHEST
import com.example.bikesoftware.presentation.maps.Altitude.LOWEST
import com.example.bikesoftware.ui.theme.TransparentBlack

@Composable
fun SummaryScreen(viewModel: MapViewModel) {

    Box {
        Card(
            modifier = Modifier
                .height(dimensionResource(R.dimen.card_size))
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.mid_padding))
                .align(Alignment.Center),
            backgroundColor = TransparentBlack,
            shape = RoundedCornerShape(dimensionResource(R.dimen.small_padding)),
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .padding(dimensionResource(R.dimen.small_padding)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.trip_summary),
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier
                        .padding(top = dimensionResource(R.dimen.small_padding)),
                    text = stringResource(R.string.trip_time, viewModel.getTripTimeSummary()),
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.average_speed, viewModel.getAverageSpeed()),
                    color = Color.White
                )

                Text(
                    text = stringResource(R.string.highest_speed, viewModel.getHighestSpeed()),
                    color = Color.White
                )

                Text(
                    text = if (viewModel.getTripDistance().first > 0) {
                        stringResource(R.string.distance_in_kilometers, viewModel.getTripDistance().first, viewModel.getTripDistance().second.toInt())
                    } else {
                        stringResource(R.string.distance, viewModel.getTripDistance().second)
                    },
                    color = Color.White
                )

                val lowestAltitude = viewModel.getAltitude(LOWEST)
                if (lowestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.lowest_altitude, lowestAltitude),
                        color = Color.White
                    )
                }

                val highestAltitude = viewModel.getAltitude(HIGHEST)
                if (highestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.highest_altitude, highestAltitude),
                        color = Color.White
                    )
                }

                if (highestAltitude > 0 && lowestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.altitude_diff, highestAltitude - lowestAltitude),
                        color = Color.White
                    )
                }
            }
        }
    }
}
