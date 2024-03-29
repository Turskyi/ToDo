package io.github.turskyi.todo.ui.tasks

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.turskyi.todo.data.*
import io.github.turskyi.todo.ui.ADD_TASK_RESULT_OK
import io.github.turskyi.todo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    state: SavedStateHandle
) : ViewModel() {

    val searchQuery: MutableLiveData<String> = state.getLiveData("searchQuery", "")

    val preferencesFlow: Flow<FilterPreferences> = preferencesManager.preferencesFlow

    private val tasksEventChannel: Channel<TasksEvent> = Channel()
    val tasksEvent: Flow<TasksEvent> = tasksEventChannel.receiveAsFlow()

    private val tasksFlow: Flow<List<TaskEntity>> = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query: String, filterPreferences: FilterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query: String, filterPreferences: FilterPreferences) ->
        taskDao.getTasks(
            query = query,
            sortOrder = filterPreferences.sortOrder,
            hideCompleted = filterPreferences.hideCompleted,
        )
    }

    val tasks: LiveData<List<TaskEntity>> = tasksFlow.asLiveData()

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: TaskEntity) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: TaskEntity, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: TaskEntity) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: TaskEntity) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: TaskEntity) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: TaskEntity) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
    }
}