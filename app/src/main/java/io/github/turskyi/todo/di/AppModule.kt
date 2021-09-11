package io.github.turskyi.todo.di
import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.turskyi.todo.data.TaskDao
import io.github.turskyi.todo.data.TaskDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TaskDatabase.Callback
    ):TaskDatabase = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
//        TODO: remove before release
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase):TaskDao = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope():CoroutineScope = CoroutineScope(SupervisorJob())
}
