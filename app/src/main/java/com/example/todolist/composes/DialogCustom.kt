package com.example.todolist.composes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun DialogCustom(
    dialogTitle: String,
    dialogText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    icon: ImageVector,
    showDialog: Boolean,
    modifier: Modifier = Modifier,
) {
    if (showDialog) {
        AlertDialog(
            icon = { Icon(icon, contentDescription = "Example Icon", tint = Color.LightGray) },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogText) },
            onDismissRequest = { onDismiss() },
            confirmButton = { TextButton(onClick = { onConfirm() }) { Text("Xác nhận") } },
            dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Hủy") } },
            modifier = modifier,
        )
    }
}
