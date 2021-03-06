package io.github.turskyi.todo.ui.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.turskyi.todo.data.TaskDao
import io.github.turskyi.todo.data.TaskEntity
import io.github.turskyi.todo.ui.ADD_TASK_RESULT_OK
import io.github.turskyi.todo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle
) : ViewModel() {

    val task: TaskEntity? = state.get<TaskEntity>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val addEditTaskEventChannel: Channel<AddEditTaskEvent> = Channel()
    val addEditTaskEvent: Flow<AddEditTaskEvent> = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            viewModelScope.launch {
                addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage("Name cannot be empty"))
            }
            return
        }

        if (task != null) {
            val updatedTask: TaskEntity = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = TaskEntity(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task: TaskEntity) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: TaskEntity) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}