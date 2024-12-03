package com.digitalcash.soarapoc.core.ui_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.digitalcash.soarapoc.R

@ExperimentalMaterial3Api
@Composable
fun CustomBaseDialog(
    modifier: Modifier = Modifier,
    showDialog: (Boolean) -> Unit = {},
    isLoadingDialog: Boolean = false,
    title: String = "Sora",
    message: (@Composable () -> Unit)? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    positiveButton: String? = null,
    negativeButton: Int? = null,
    iconResource: Int? = null,
    positiveButtonColor: Color = MaterialTheme.colorScheme.primary,
    onPositiveCallback: () -> Unit = {},
    onNegativeCallback: () -> Unit = {},
    onCloseCallback: (() -> Unit)? = null,
) {
    BasicAlertDialog(
        onDismissRequest = { showDialog(false) },
        modifier = Modifier
            .width(312.dp)
            .clipToBounds()
            .clip(RoundedCornerShape(2.dp)),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(22.dp)),
        ) {
            if (isLoadingDialog) {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            22.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    onCloseCallback?.let {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(16.dp)
                                .clickable { it() },
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Close Dialog",
                        )
                    }

                    iconResource?.let {
                        Image(
                            modifier = Modifier.size(80.dp),
                            painter = painterResource(id = iconResource),
                            contentDescription = "",
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            color = titleColor,
                        ),
                    )

                    message?.let {
                        it()
                    }


                    Column(
                        Modifier.padding(vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                        ),
                    ) {
                        positiveButton?.let {
                            AppButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                title = positiveButton,
                                enabled = true,
                                enableButtonColor = positiveButtonColor,
                            ) {
                                onPositiveCallback()
                            }
                        }

                        negativeButton?.let {
                            AppSecondaryButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                title = stringResource(id = negativeButton),
                            ) {
                                onNegativeCallback()
                            }
                        }
                    }
                }
            }
        }
    }
}