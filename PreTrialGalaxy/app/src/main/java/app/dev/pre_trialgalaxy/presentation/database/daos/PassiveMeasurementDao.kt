package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import app.dev.pre_trialgalaxy.presentation.database.entities.PassiveMeasurement

@Dao
interface PassiveMeasurementDao {

    @Insert
    suspend fun insert(passive: PassiveMeasurement) : Long

}