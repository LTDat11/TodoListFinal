package com.example.todolist.domain.usecase

import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskUseCase
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) {
        operator fun invoke(): Flow<List<Task>> = repository.getTasks()
    }
