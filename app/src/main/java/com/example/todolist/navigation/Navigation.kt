package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.presentation.ToDoListViewModel
import com.example.todolist.presentation.screens.TaskDetailScreen
import com.example.todolist.presentation.screens.TodoListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "todo_list") {
        composable("todo_list") { TodoListScreen(navController = navController) }
        composable("task_detail/{taskId}") { backStackEntry ->
            val taskId =
                backStackEntry.arguments?.getString("taskId")?.toLong() ?: 0L
            val viewModel = hiltViewModel<ToDoListViewModel>()
            TaskDetailScreen(taskId = taskId, viewModel = viewModel, navController = navController)
        }
    }
}
