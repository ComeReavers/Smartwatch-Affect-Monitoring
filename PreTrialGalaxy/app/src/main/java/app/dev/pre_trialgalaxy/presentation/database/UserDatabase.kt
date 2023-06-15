package app.dev.pre_trialgalaxy.presentation.database

import androidx.room.*
import app.dev.pre_trialgalaxy.presentation.database.daos.*
import app.dev.pre_trialgalaxy.presentation.database.entities.*

/*
The Database handles all the classes and methods that interact with the database.
 */
@Database(version = 5,
    entities = [ActiveMeasurement::class,
        InteractionMeasurement::class,
        NonHealthMeasurement::class,
        NotificationData::class,
        PassiveMeasurement::class,
        StudyData::class,
        AffectData::class
    ]
)
abstract class UserDatabase: RoomDatabase(){
    abstract fun getActiveMeasurementDao(): ActiveMeasurementDao
    abstract fun getInteractionMeasurementDao(): InteractionMeasurementDao
    abstract fun getPassiveMeasurementDao(): PassiveMeasurementDao
    abstract fun getStudyDataDao(): StudyDao
    abstract fun getAffectDao(): AffectDao
    abstract fun getNotificationDao(): NotificationDao
}
