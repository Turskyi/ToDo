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

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(TaskEntity("Wash the dishes"))
                dao.insert(TaskEntity("Do the laundry"))
                dao.insert(TaskEntity("Buy groceries", important = true))
                dao.insert(TaskEntity("Prepare food", completed = true))
                dao.insert(TaskEntity("Call mom"))
                dao.insert(TaskEntity("Visit grandma", completed = true))
                dao.insert(TaskEntity("Repair my bike"))
                dao.insert(TaskEntity("Call Elon Musk"))
            }
        }
    }
}