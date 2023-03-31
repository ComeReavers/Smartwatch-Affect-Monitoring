package app.dev.pre_trialgalaxy.presentation.database.daos

import androidx.room.*
import app.dev.pre_trialgalaxy.presentation.database.entities.ActiveMeasurement

@Dao
interface ActiveMeasurementDao {

    @Insert
    suspend fun insert(active : ActiveMeasurement): Long

    @Insert
    suspend fun insertAll(activeList: List<ActiveMeasurement>): List<Long>
}