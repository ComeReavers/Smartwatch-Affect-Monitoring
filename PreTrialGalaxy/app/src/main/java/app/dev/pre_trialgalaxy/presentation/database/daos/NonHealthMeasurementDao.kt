package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.PrimaryKey
import app.dev.pre_trialgalaxy.presentation.database.entities.NonHealthMeasurement

@Dao
interface NonHealthMeasurementDao{

    @Insert
    suspend fun insert(vararg study: NonHealthMeasurement): Long
}