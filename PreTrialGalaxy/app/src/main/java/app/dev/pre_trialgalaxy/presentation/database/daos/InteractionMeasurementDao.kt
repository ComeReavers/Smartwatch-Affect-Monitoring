package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.Dao
import androidx.room.Insert
import app.dev.pre_trialgalaxy.presentation.database.entities.InteractionMeasurement

@Dao
interface InteractionMeasurementDao {

    @Insert
    suspend fun insert(vararg: InteractionMeasurement) : Long
}