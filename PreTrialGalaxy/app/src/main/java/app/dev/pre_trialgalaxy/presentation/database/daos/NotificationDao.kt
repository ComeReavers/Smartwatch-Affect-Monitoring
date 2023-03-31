package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import app.dev.pre_trialgalaxy.presentation.database.entities.NotificationData

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationData): Long
}