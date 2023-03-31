package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import app.dev.pre_trialgalaxy.presentation.database.entities.AffectData

@Dao
interface AffectDao {

    @Insert
    suspend fun insert(affect: AffectData): Long
}