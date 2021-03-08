package io.github.turskyi.todo.data

import androidx.room.*
import io.github.turskyi.todo.data.TaskEntity.Companion.TABLE_TASKS
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM $TABLE_TASKS")
    fun getTasks(): Flow<List<TaskEntity>>

    /* "suspend" added to function for using in the background thread */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}