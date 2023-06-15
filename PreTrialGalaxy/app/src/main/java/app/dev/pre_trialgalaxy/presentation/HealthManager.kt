package app.dev.pre_trialgalaxy.presentation

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.PassiveListenerCallback
import androidx.health.services.client.data.*
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

/*
The HealthManager is used to determine the capabilities of the smartwatch and to interact with the
sensors.
 */
@Deprecated("This class and it's functions are no longer in use.",
    ReplaceWith("Samsung Privileged Health SDK functionality"))
class HealthManager(private val context: Context) {
    private val logTag = "HealthManager"

    private val healthClient = HealthServices.getClient(context)
    private val measureClient = healthClient.measureClient
    private val exerciseClient = healthClient.exerciseClient
    private val passiveClient = healthClient.passiveMonitoringClient

    private val passiveDataTypes = setOf(
        DataType.HEART_RATE_BPM, DataType.DISTANCE,
        DataType.CALORIES, DataType.SPEED, DataType.WALKING_STEPS, DataType.RUNNING_STEPS
    )
    private val activeDataType = DataType.Companion.HEART_RATE_BPM

    private val exerciseType = ExerciseType.WALKING
    private val exerciseDataTypes = setOf(
        DataType.LOCATION, DataType.CALORIES, DataType.SPEED,
        DataType.PACE, DataType.STEPS_PER_MINUTE, DataType.VO2_MAX, DataType.HEART_RATE_BPM
    )

    private var heartRateMeasurements: MutableList<DataPointContainer> = mutableListOf()

    private val heartRateCallback = object : MeasureCallback {
        override fun onAvailabilityChanged(
            dataType: DeltaDataType<*, *>,
            availability: Availability
        ) {
            // Nothing to do, availability won't change
        }

        override fun onDataReceived(data: DataPointContainer) {
            //TODO DataPoint into Database
            val currentTime = LocalDateTime.now()
            heartRateMeasurements.add(data)
            Log.v("ActiveData", "Received active Data: " +
                    "${data.getData(activeDataType).size} at $currentTime")
        }
    }

    fun getMeasureCapabilities(): ListenableFuture<MeasureCapabilities> {
        val capabilities: ListenableFuture<MeasureCapabilities> =
            measureClient.getCapabilitiesAsync()

        Futures.addCallback(
            capabilities,
            object : FutureCallback<MeasureCapabilities> {
                override fun onSuccess(result: MeasureCapabilities?) {
                    val supportedMeasurements = result?.supportedDataTypesMeasure
                    Log.v(logTag, supportedMeasurements.toString())
                }

                override fun onFailure(t: Throwable) {
                    print("failure!!")
                }

            }, context.mainExecutor
        )
        return capabilities
    }

    fun getExerciseCapabilities(): ListenableFuture<ExerciseCapabilities> {
        val capabilities: ListenableFuture<ExerciseCapabilities> =
            exerciseClient.getCapabilitiesAsync()

        Futures.addCallback(
            capabilities,
            object : FutureCallback<ExerciseCapabilities> {
                override fun onSuccess(result: ExerciseCapabilities?) {
                    val supportedMeasurements = result!!.supportedExerciseTypes
                    if (ExerciseType.WALKING in supportedMeasurements) {
                        val capabilities = result.getExerciseTypeCapabilities(ExerciseType.WALKING)
                    }
                    Log.v(
                        logTag,
                        capabilities.get().getExerciseTypeCapabilities(ExerciseType.WALKING)
                            .toString()
                    )
                }

                override fun onFailure(t: Throwable) {
                    Log.v(logTag, "failure!!")
                }

            }, context.mainExecutor
        )
        return capabilities
    }

    fun getPassiveCapabilities(): ListenableFuture<PassiveMonitoringCapabilities> {
        val capabilities: ListenableFuture<PassiveMonitoringCapabilities> =
            passiveClient.getCapabilitiesAsync()

        Futures.addCallback(
            capabilities,
            object : FutureCallback<PassiveMonitoringCapabilities> {
                override fun onSuccess(result: PassiveMonitoringCapabilities?) {
                    val supportedMeasurements = result?.supportedDataTypesPassiveMonitoring
                    Log.v(logTag, supportedMeasurements.toString())
                }

                override fun onFailure(t: Throwable) {
                    print("failure!!")
                }

            }, context.mainExecutor
        )
        return capabilities
    }

    fun startHeartRateMeasurements() {
        HealthServices.getClient(context)
            .measureClient
            .registerMeasureCallback(activeDataType, heartRateCallback)
    }

    // disengage the listeners and put the collected data into the db
    fun stopHeartRateMeasurements() {
        Log.v("HeartRateMeasurement", "stop has been called!")
        runBlocking {
            HealthServices.getClient(context)
                .measureClient
                .unregisterMeasureCallbackAsync(activeDataType, heartRateCallback)
        }

        UserDataStore
            .getUserRepository(context)
            .handleHeartRateDataList(
                heartRateMeasurements,
                CoroutineScope(GlobalScope.coroutineContext)
            ) {
                Log.v("HeartRateMeasurement", "Inserted data into database")
                Log.v("HeartRateMeasurement", "$heartRateMeasurements")
            }
    }

    fun engagePassiveListener(context: Context) {
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setDataTypes(passiveDataTypes)
            .build()
        val passiveListenerCallback: PassiveListenerCallback = object : PassiveListenerCallback {
            override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {

                val currentTime = LocalDateTime.now()
                Log.v("PassiveData", "Received passive Data: $dataPoints at $currentTime")
            }
        }
        passiveClient.setPassiveListenerCallback(
            passiveListenerConfig,
            passiveListenerCallback
        )
    }

    fun disengageActiveListener(context: Context) {
        measureClient.unregisterMeasureCallbackAsync(activeDataType, heartRateCallback)
    }

    fun disengagePassiveListener(context: Context) {
        //TODO check if awaitClose{} and runBlocking{} is necessary (yes, it probably is)
        passiveClient.clearPassiveListenerCallbackAsync()
    }

    suspend fun startStudy() {

    }

}