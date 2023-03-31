package app.dev.pre_trialgalaxy.presentation.database.entities

import androidx.health.services.client.data.DataPoint
import androidx.health.services.client.data.DeltaDataType
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActiveMeasurement(

    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var notificationId: Long,
    var timestamp: String,
    var heartRate: Double,
    var heartRateAccuracy: String?
)