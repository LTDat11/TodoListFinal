package com.example.todolist.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.usecase.AddTaskUseCase
import com.example.todolist.domain.usecase.DeleteTaskUseCase
import com.example.todolist.domain.usecase.GetTaskUseCase
import com.example.todolist.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToDoListViewModel
    @Inject
    constructor(
        private val getTaskUseCase: GetTaskUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _tasks = MutableStateFlow<List<Task>>(emptyList())
        private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
        val tasks: StateFlow<List<Task>> = _filteredTasks.asStateFlow() // Hiển thị danh sách đã lọc

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

        // State cho TaskDetailScreen
        private val _selectedTask = MutableStateFlow<Task?>(null)
        // val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

        private val _titleText = MutableStateFlow("")
        val titleText: StateFlow<String> = _titleText.asStateFlow()

        private val _contentText = MutableStateFlow("")
        val contentText: StateFlow<String> = _contentText.asStateFlow()

        private val _isUpdateEnabled = MutableStateFlow(false)
        val isUpdateEnabled: StateFlow<Boolean> = _isUpdateEnabled.asStateFlow()

        // Loading detail
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        // Loading listView
        private val _isTaskLoading = MutableStateFlow(true)
        val isTaskLoading: StateFlow<Boolean> = _isTaskLoading.asStateFlow()

        // showDialog
        private val _showConfirmDialog = MutableStateFlow(false)
        val showConfirmDialog: StateFlow<Boolean> = _showConfirmDialog.asStateFlow()

        private val _taskToDelete = MutableStateFlow<Task?>(null)
        // val taskToDelete: StateFlow<Task?> = _taskToDelete.asStateFlow()

        // dialog xác nhận xóa
        private val _showDeleteConfirmDialog = MutableStateFlow(false)
        val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()

        // State cho vị trí FAB
        private val _fabOffsetX = MutableStateFlow(savedStateHandle.get<Float>("fab_x") ?: 0f)
        val fabOffsetX: StateFlow<Float> = _fabOffsetX.asStateFlow()

        private val _fabOffsetY = MutableStateFlow(savedStateHandle.get<Float>("fab_y") ?: 0f)
        val fabOffsetY: StateFlow<Float> = _fabOffsetY.asStateFlow()

        init {
            viewModelScope.launch {
                getTaskUseCase().collect { allTasks ->
                    _tasks.value = allTasks
                    filterTasks() // Lọc task ban đầu
                    delay(500)
                    _isTaskLoading.value = false
                }
            }
        }

        // Cập nhật truy vấn tìm kiếm và lọc task
        fun onSearchQueryChanged(query: String) {
            _searchQuery.value = query
            filterTasks()
        }

        // Logic lọc task dựa trên truy vấn
        private fun filterTasks() {
            val query = _searchQuery.value.trim()
            _filteredTasks.value =
                if (query.isEmpty()) {
                    _tasks.value // Hiển thị toàn bộ task nếu query rỗng
                } else {
                    _tasks.value.filter { task ->
                        task.title.contains(query, ignoreCase = true) ||
                            task.content.contains(query, ignoreCase = true)
                    }
                }
        }

        fun addTask(
            title: String,
            content: String,
        ) {
            val task = Task(title = title, content = content)
            viewModelScope.launch { addTaskUseCase(task) }
        }

        private fun updateTask(task: Task) {
            viewModelScope.launch { updateTaskUseCase(task) }
        }

        // Update trạng thái của ták
        fun onTaskStatusChanged(
            task: Task,
            isCompleted: Boolean,
        ) {
            val updatedTask = task.copy(isCompleted = isCompleted)
            updateTask(updatedTask)
        }

        // Load task khi vào TaskDetailScreen
        fun loadTask(taskId: Long) {
            viewModelScope.launch {
                _isLoading.value = true // Show loading
                _tasks.collect { tasks ->
                    // println("ViewModel: Current tasks = $tasks")
                    val task = tasks.find { it.id == taskId }
                    _selectedTask.value = task
                    // println("ViewModel: Found task = $task")
                    task?.let {
                        _titleText.value = it.title
                        _contentText.value = it.content
                        updateButtonState()
                    }
                    delay(500)
                    _isLoading.value = false
                    return@collect
                }
            }
        }

        // Cập nhật title từ UI
        fun onTitleChanged(newTitle: String) {
            _titleText.value = newTitle
            updateButtonState()
        }

        // Cập nhật content từ UI
        fun onContentChanged(newContent: String) {
            _contentText.value = newContent
            updateButtonState()
        }

        // Kiểm tra dữ liệu thay đổi để enable button
        private fun updateButtonState() {
            val task = _selectedTask.value ?: return
            _isUpdateEnabled.value =
                (_titleText.value != task.title || _contentText.value != task.content) &&
                _titleText.value.isNotEmpty()
        }

        fun onUpdateTaskRequested() {
            if (_isUpdateEnabled.value) {
                _showConfirmDialog.value = true // Hiển thị dialog xác nhận
            }
        }

        fun onConfirmUpdate() {
            val task = _selectedTask.value ?: return
            val updatedTask = task.copy(title = _titleText.value, content = _contentText.value)
            updateTask(updatedTask)
            _showConfirmDialog.value = false // Ẩn dialog sau khi xác nhận
        }

        fun onDismissDialog() {
            _showConfirmDialog.value = false // Ẩn dialog khi hủy
        }

        // Xóa task
        fun onDeleteTaskRequested(task: Task) {
            _taskToDelete.value = task
            _showDeleteConfirmDialog.value = true
        }

        fun onConfirmDelete() {
            val task = _taskToDelete.value ?: return
            viewModelScope.launch {
                deleteTaskUseCase(task)
                _showDeleteConfirmDialog.value = false
                _taskToDelete.value = null
            }
        }

        fun onDismissDeleteDialog() {
            _showDeleteConfirmDialog.value = false
            _taskToDelete.value = null
        }

        // Hàm cập nhật vị trí FAB
        fun updateFabPosition(
            x: Float,
            y: Float,
        ) {
            _fabOffsetX.value = x
            _fabOffsetY.value = y
            savedStateHandle["fab_x"] = x
            savedStateHandle["fab_y"] = y
        }

        // Kéo FAB
        fun onFabDragged(
            dragAmountX: Float,
            dragAmountY: Float,
            screenWidth: Float,
            screenHeight: Float,
            density: Float,
        ) {
            val fabSize = 56f
            val newOffsetX =
                (_fabOffsetX.value + dragAmountX / density).coerceIn(0f, screenWidth - fabSize)
            val newOffsetY =
                (_fabOffsetY.value + dragAmountY / density).coerceIn(0f, screenHeight - fabSize)
            _fabOffsetX.value = newOffsetX
            _fabOffsetY.value = newOffsetY
        }

        // Thả Fab kéo về trái hoặc phải
        fun onFabDragEnded(screenWidth: Float) {
            val fabSize = 56f
            val halfScreen = screenWidth / 2
            val targetX = if (_fabOffsetX.value < halfScreen) 0f else (screenWidth - fabSize)
            _fabOffsetX.value = targetX
            savedStateHandle["fab_x"] = targetX
            savedStateHandle["fab_y"] = _fabOffsetY.value
        }

        // Điều chỉnh vị trí FAB khi xoay màn hình hoặc khởi tạo
        fun adjustFabPosition(
            screenWidth: Float,
            screenHeight: Float,
        ) {
            val fabSize = 56f
            val adjustedX = _fabOffsetX.value.coerceIn(0f, screenWidth - fabSize)
            val adjustedY = _fabOffsetY.value.coerceIn(0f, screenHeight - fabSize)

            if (_fabOffsetX.value == 0f && _fabOffsetY.value == 0f) {
                // Đặt vị trí mặc định nếu chưa có
                val defaultX = screenWidth - fabSize - 16f
                val defaultY = screenHeight - fabSize - 16f
                _fabOffsetX.value = defaultX
                _fabOffsetY.value = defaultY
                savedStateHandle["fab_x"] = defaultX
                savedStateHandle["fab_y"] = defaultY
            } else {
                // Chỉ điều chỉnh nếu ra ngoài màn hình
                _fabOffsetX.value = adjustedX
                _fabOffsetY.value = adjustedY
                savedStateHandle["fab_x"] = adjustedX
                savedStateHandle["fab_y"] = adjustedY
            }
        }
    }
