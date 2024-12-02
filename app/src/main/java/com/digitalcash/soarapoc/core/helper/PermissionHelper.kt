package com.digitalcash.googlemap.core.helper

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permission: String,
    content: @Composable () -> Unit = { },
) {
    val permissionState = rememberPermissionState(permission)

    if (permissionState.status.isGranted) {
        content()
    } else {
        Rationale(text = "Location permission required for this feature to be available. ") {
            permissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "permission_title")
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(text = "ok")
            }
        },
    )
}