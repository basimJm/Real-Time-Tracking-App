package com.digitalcash.soarapoc.core.ui_component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    buttonHeight: Dp = 60.dp,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
    elevation: Dp = 0.dp,
    border: BorderStroke? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        textAlign = TextAlign.Center,
        lineHeight = 27.sp,
        color = MaterialTheme.colorScheme.onPrimary,
    ),
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    enableButtonColor: Color = MaterialTheme.colorScheme.primary,
    disableButtonColor: Color = Color.Gray,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onClick: () -> Unit,
) {

    ElevatedButton(
        border = border,
        modifier = modifier
            .height(buttonHeight),
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = elevation,
        ),
        colors = ButtonDefaults.elevatedButtonColors(
            disabledContainerColor = disableButtonColor,
            containerColor = enableButtonColor,
            contentColor = textColor,
            disabledContentColor = MaterialTheme.colorScheme.onError,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!isLoading) {
                leadingIcon?.let {
                    Icon(imageVector = it, contentDescription = "")
                }

                Text(
                    text = title,
                    style = textStyle,
                )

                trailingIcon?.let {
                    Icon(imageVector = it, contentDescription = "")
                }
            } else {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun AppSecondaryButton(
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    buttonHeight: Dp = 60.dp,
    trailingIcon: ImageVector? = null,
    leadingIcon: Int? = null,
    leadingIconSize: Dp = 22.dp,
    elevation: Dp = 0.dp,
    border: BorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary,
    ),
    disableButtonBorder: BorderStroke = BorderStroke(
        width = 1.dp,
        color = Color.Gray,
    ),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        textAlign = TextAlign.Center,
        lineHeight = 27.sp,
        color = MaterialTheme.colorScheme.primary,
    ),
    textColor: Color = MaterialTheme.colorScheme.primary,
    enableButtonColor: Color = MaterialTheme.colorScheme.surface,
    disableButtonColor: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    onClick: () -> Unit,
) {


    ElevatedButton(
        border = if (enabled) border else disableButtonBorder,
        modifier = modifier
            .height(buttonHeight),
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = elevation,
        ),
        colors = ButtonDefaults.elevatedButtonColors(
            disabledContainerColor = disableButtonColor,
            containerColor = enableButtonColor,
            contentColor = textColor,
            disabledContentColor = MaterialTheme.colorScheme.onError,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isLoading) {
                leadingIcon?.let {
                    Icon(
                        modifier = Modifier.size(leadingIconSize),
                        painter = painterResource(id = it),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    text = title,
                    style = textStyle.copy(
                        color = if (enabled) textColor else Color.Gray,
                    ),
                )

                trailingIcon?.let {
                    Icon(imageVector = it, contentDescription = "")
                }
            } else {
                CircularProgressIndicator(
                    modifier = loadingModifier,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}