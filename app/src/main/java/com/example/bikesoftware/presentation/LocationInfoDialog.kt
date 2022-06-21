package com.example.bikesoftware.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bikesoftware.R
import com.example.bikesoftware.ui.theme.Orange

@Composable
fun LocationInfoDialog(onGoToSettingsClick: () -> Unit) {

    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = stringResource(id = R.string.location_permission_title),
                    fontSize = dimensionResource(R.dimen.mid_text).value.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.lexend_thin))
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.location_permission_description),
                    color = Orange,
                    fontSize = dimensionResource(R.dimen.very_small_text).value.sp,
                    fontFamily = FontFamily(Font(R.font.lexend_thin)),
                    fontWeight = FontWeight.Bold
                )
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = dimensionResource(R.dimen.small_padding)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            openDialog.value = false
                            onGoToSettingsClick()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Orange),
                    ) {
                        Text(
                            text = stringResource(R.string.go_to_settings),
                            fontFamily = FontFamily(Font(R.font.lexend_thin)),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        )
    }
}
