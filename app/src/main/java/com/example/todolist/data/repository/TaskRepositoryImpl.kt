package com.example.todolist.data.repository

import android.util.Log
import com.example.todolist.ObjectBox
import com.example.todolist.data.model.TaskEntity
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.TaskRepository
import io.objectbox.Box
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class TaskRepositoryImpl
    @Inject
    constructor() : TaskRepository {
        private val taskBox: Box<TaskEntity> by lazy { ObjectBox.store.boxFor(TaskEntity::class.java) }

        override fun getTasks(): Flow<List<Task>> =
            callbackFlow {
                val query = taskBox.query().build()
                val subscription =
                    query.subscribe().observer { taskEntities ->
                        val tasks = taskEntities.map { it.toDomain() } // ánh xạ qua task
                        trySend(tasks)
                    }
                awaitClose { subscription.cancel() }
            }

        override suspend fun addTask(task: Task) {
            Log.d("TaskRepositoryImpl", "Adding task: $task")
            taskBox.put(task.toEntity())
        }

        override suspend fun updateTask(task: Task) {
            Log.d("TaskRepositoryImpl", "update task: $task")
            taskBox.put(task.toEntity())
        }

        override suspend fun deleteTask(task: Task) {
            Log.d("TaskRepositoryImpl", "delete task: $task")
            taskBox.remove(task.toEntity())
        }

        // Hàm ánh xạ từ TaskEntity sang Task
        private fun TaskEntity.toDomain(): Task = Task(id = id, title = title, content = content, isCompleted = isCompleted)

        // Hàm ánh xạ từ Task sang TaskEntity
        private fun Task.toEntity(): TaskEntity = TaskEntity(id = id, title = title, content = content, isCompleted = isCompleted)
    }
