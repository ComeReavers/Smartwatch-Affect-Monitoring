package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import app.dev.pre_trialgalaxy.presentation.database.entities.StudyData

@Dao
interface StudyDao {

    @Insert
    suspend fun insert(study: StudyData): Long
}