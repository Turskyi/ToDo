package io.github.turskyi.todo.data

import androidx.room.*
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_IMPORTANT
import io.github.turskyi.todo.data.TaskEntity.Companion.COLUMN_NAME
import io.github.turskyi.todo.data.TaskEntity.Companion.TABLE_TASKS
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM $TABLE_TASKS WHERE $COLUMN_NAME LIKE '%' || :searchQuery || '%' ORDER BY $COLUMN_IMPORTANT DESC")
    fun getTasks(searchQuery: String): Flow<List<TaskEntity>>

    /* "suspend" added to function for using in the background thread */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}