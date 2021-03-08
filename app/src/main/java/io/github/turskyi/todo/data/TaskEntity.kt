package io.github.turskyi.todo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.turskyi.todo.data.TaskEntity.Companion.TABLE_TASKS
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = TABLE_TASKS)
@Parcelize
data class TaskEntity(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    companion object {
        const val TABLE_TASKS = "task_table"
    }
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}