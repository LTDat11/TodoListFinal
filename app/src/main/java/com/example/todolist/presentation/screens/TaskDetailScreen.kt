package com.example.todolist.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todolist.composes.DialogCustom
import com.example.todolist.presentation.ToDoListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    navController: NavController,
    viewModel: ToDoListViewModel = hiltViewModel(),
) {
    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    val titleText by viewModel.titleText.collectAsState()
    val contentText by viewModel.contentText.collectAsState()
    val isUpdateEnabled by viewModel.isUpdateEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showConfirmDialog by viewModel.showConfirmDialog.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Chi tiết Task",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                    colors =
                        TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                )
            }
        },
    ) { paddingValue ->
        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .padding(paddingValue)
                        .padding(16.dp)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { viewModel.onTitleChanged(it) },
                    label = { Text("Tiêu đề") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                )
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { viewModel.onContentChanged(it) },
                    label = { Text("Nội dung") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )
                Button(
                    onClick = { viewModel.onUpdateTaskRequested() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isUpdateEnabled,
                ) {
                    Text("Lưu")
                }
            }

            DialogCustom(
                dialogTitle = "Thông báo!",
                dialogText = "Bạn có chắc chắn muốn thay đổi thông tin của Task?",
                icon = Icons.Default.Info,
                onDismiss = { viewModel.onDismissDialog() },
                onConfirm = {
                    viewModel.onConfirmUpdate()
                    navController.popBackStack()
                },
                showDialog = showConfirmDialog,
            )
        }
    }
}
