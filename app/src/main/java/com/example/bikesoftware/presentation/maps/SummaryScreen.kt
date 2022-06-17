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
import com.example.bikesoftware.ui.theme.TransparentBlack

@Composable
fun SummaryScreen(viewModel: MapViewModel) {

    Box {
        Card(
            modifier = Modifier
                .height(dimensionResource(R.dimen.card_size))
                .width(dimensionResource(R.dimen.card_size))
                .align(Alignment.Center),
            backgroundColor = TransparentBlack,
            shape = RoundedCornerShape(dimensionResource(R.dimen.small_padding)),
            elevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.small_padding))
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
                        .padding(top = dimensionResource(R.dimen.mid_padding)),
                    text = stringResource(R.string.trip_time, viewModel.getTripTimeSummary()),
                    color = Color.White
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = dimensionResource(R.dimen.big_padding)),
                    text = stringResource(R.string.average_speed, viewModel.getAverageSpeed()),
                    color = Color.White
                )

                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = dimensionResource(R.dimen.large_padding)),
                    text = if (viewModel.getTripDistance().first > 0) {
                        stringResource(R.string.distance_in_kilometers, viewModel.getTripDistance().first, viewModel.getTripDistance().second.toInt())
                    } else {
                        stringResource(R.string.distance, viewModel.getTripDistance().second)
                    },
                    color = Color.White
                )
            }
        }
    }
}
