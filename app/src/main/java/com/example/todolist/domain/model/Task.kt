package com.example.todolist.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
)
