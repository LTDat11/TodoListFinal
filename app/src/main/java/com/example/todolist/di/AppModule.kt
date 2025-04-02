package com.example.todolist.di

import com.example.todolist.data.repository.TaskRepositoryImpl
import com.example.todolist.domain.repository.TaskRepository
import com.example.todolist.domain.usecase.AddTaskUseCase
import com.example.todolist.domain.usecase.DeleteTaskUseCase
import com.example.todolist.domain.usecase.GetTaskUseCase
import com.example.todolist.domain.usecase.UpdateTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideTaskRepository(): TaskRepository = TaskRepositoryImpl()

    @Provides
    @Singleton
    fun provideGetTaskUseCase(repository: TaskRepository): GetTaskUseCase = GetTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase = AddTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase = DeleteTaskUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(repository: TaskRepository): UpdateTaskUseCase = UpdateTaskUseCase(repository)
}
