package com.example.todolist.composes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolist.domain.model.Task
import com.example.todolist.presentation.ToDoListViewModel
import com.example.todolist.ui.theme.inversePrimaryLight
import com.example.todolist.ui.theme.onTertiaryLight

@Composable
fun CardCustom(
    task: Task,
    onClick: () -> Unit,
    viewModel: ToDoListViewModel = hiltViewModel(),
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (task.isCompleted) {
                        onTertiaryLight
                    } else {
                        inversePrimaryLight
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { isChecked -> viewModel.onTaskStatusChanged(task, isChecked) },
                modifier = Modifier.padding(8.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style =
                        if (task.isCompleted) {
                            MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = TextDecoration.LineThrough,
                            )
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                )
                Text(
                    text = task.content,
                    style =
                        if (task.isCompleted) {
                            MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough,
                            )
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }

            if (task.isCompleted) {
                IconButton(onClick = { viewModel.onDeleteTaskRequested(task) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
