package com.example.bikesoftware.presentation.maps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikesoftware.R
import com.example.bikesoftware.presentation.maps.Altitude.HIGHEST
import com.example.bikesoftware.presentation.maps.Altitude.LOWEST
import com.example.bikesoftware.ui.theme.Orange
import com.example.bikesoftware.ui.theme.TransparentBlack65

@Composable
fun SummaryScreen(viewModel: MapViewModel, onCloseClick: () -> Unit) {

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .height(dimensionResource(R.dimen.card_height))
                .padding(
                    start = dimensionResource(R.dimen.mid_padding),
                    end = dimensionResource(R.dimen.mid_padding)
                ),
            backgroundColor = TransparentBlack65,
            shape = RoundedCornerShape(dimensionResource(R.dimen.small_padding)),
            elevation = 0.dp
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onCloseClick() },
                    modifier = Modifier.padding(dimensionResource(R.dimen.small_padding))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Orange,
                        modifier = Modifier
                            .height(dimensionResource(R.dimen.icon_size))
                            .width(dimensionResource(R.dimen.icon_size))
                    )
                }
            }

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
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontSize = dimensionResource(R.dimen.mid_text).value.sp,
                )

                Text(
                    modifier = Modifier
                        .padding(top = dimensionResource(R.dimen.small_padding)),
                    text = stringResource(R.string.trip_time, viewModel.getTripTimeSummary()),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp
                )

                Text(
                    text = stringResource(R.string.average_speed, viewModel.getAverageSpeed()),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp
                )

                Text(
                    text = stringResource(R.string.highest_speed, viewModel.getHighestSpeed()),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp
                )

                Text(
                    text = if (viewModel.getTripDistance().first > 0) {
                        stringResource(R.string.distance_in_kilometers, viewModel.getTripDistance().first, viewModel.getTripDistance().second.toInt())
                    } else {
                        stringResource(R.string.distance, viewModel.getTripDistance().second)
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontSize = dimensionResource(R.dimen.small_text).value.sp
                )

                val lowestAltitude = viewModel.getAltitude(LOWEST)
                if (lowestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.lowest_altitude, lowestAltitude),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexend_thin)),
                        fontSize = dimensionResource(R.dimen.small_text).value.sp
                    )
                }

                val highestAltitude = viewModel.getAltitude(HIGHEST)
                if (highestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.highest_altitude, highestAltitude),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexend_thin)),
                        fontSize = dimensionResource(R.dimen.small_text).value.sp
                    )
                }

                if (highestAltitude > 0 && lowestAltitude > 0) {
                    Text(
                        text = stringResource(R.string.altitude_diff, highestAltitude - lowestAltitude),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.lexend_thin)),
                        fontSize = dimensionResource(R.dimen.small_text).value.sp
                    )
                }
            }
        }
    }
}
