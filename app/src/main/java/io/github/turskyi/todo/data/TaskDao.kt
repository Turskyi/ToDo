package io.github.turskyi.todo.data

import androidx.room.*
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_COMPLETED
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_CREATED
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_IMPORTANT
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_NAME
import io.github.turskyi.todo.data.TaskEntity.Companion.TABLE_TASKS
import io.github.turskyi.todo.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TaskEntity>> =
        when(sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM $TABLE_TASKS WHERE ($COLUMN_COMPLETED != :hideCompleted OR $COLUMN_COMPLETED = 0) AND $COLUMN_NAME LIKE '%' || :searchQuery || '%' ORDER BY $COLUMN_IMPORTANT DESC, $COLUMN_NAME")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM $TABLE_TASKS WHERE ($COLUMN_COMPLETED != :hideCompleted OR $COLUMN_COMPLETED = 0) AND $COLUMN_NAME LIKE '%' || :searchQuery || '%' ORDER BY $COLUMN_IMPORTANT DESC, $COLUMN_CREATED")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<TaskEntity>>

    /* "suspend" added to function for using in the background thread */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}