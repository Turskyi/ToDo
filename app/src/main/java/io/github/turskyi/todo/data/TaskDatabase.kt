package io.github.turskyi.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.turskyi.todo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao: TaskDao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(TaskEntity(name = "Wash the dishes"))
                dao.insert(TaskEntity(name = "Do the laundry"))
                dao.insert(TaskEntity(name = "Buy groceries", important = true))
                dao.insert(TaskEntity(name = "Prepare food", completed = true))
                dao.insert(TaskEntity(name = "Call mom"))
                dao.insert(TaskEntity(name = "Visit grandma", completed = true))
                dao.insert(TaskEntity(name = "Repair my bike"))
                dao.insert(TaskEntity(name = "Call Elon Musk"))
            }
        }
    }
}