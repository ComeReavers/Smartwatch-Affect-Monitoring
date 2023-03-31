package app.dev.pre_trialgalaxy.presentation

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.*
import androidx.work.*
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import app.dev.pre_trialgalaxy.presentation.database.entities.StudyData
import app.dev.pre_trialgalaxy.presentation.theme.PreTrialGalaxyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/*  facilitating the start (pause) and end buttons for the survey. Will initialize the WorkRequests,
    the DB and the listeners to the active and passive healthdata streams.
    Afterwards it will start the first prompt-cycle and close.
    When the stop button is hit, the Listeners will be disengaged,
    (the data will be copied into another DB?) and the WorkRequests will be stopped.
    On the next Screen, stats about the participant will be shown, e.g. how often he interacted with
    the prompts.
 */

class StudyStartActivity : ComponentActivity() {

    private val requestedPermissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WAKE_LOCK
    )

    private val promptFrequency = 15L
    private val promptFrequencyTimeUnit = TimeUnit.MINUTES

    //TODO adjust initialDelay before study
    private val initialDelay = 1L
    private val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        promptFrequency,
        promptFrequencyTimeUnit
    )
        .setInitialDelay(initialDelay, promptFrequencyTimeUnit)
        .addTag("notification")
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestPermissions(requestedPermissions, 0)
        super.onCreate(savedInstanceState)
        setContent {
            StudyStartApp()
        }
    }

    @Composable
    fun StudyStartApp() {
        //TODO add confirmation screens/change button colors depending on state
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item { //Start
                    Chip(
                        label = {
                            Text(text = "Start")
                        },
                        onClick = {
                            initializeStudy()
                        },

                    )
                }

                item { //Stop
                    Chip(
                        label = {
                            Text(text = "Stop")
                        },
                        onClick = {
                            stopStudy()
                        }
                    )
                }
            }
        }
    }

    private fun initializeStudy() {
        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        UserDataStore
            .getUserRepository(this)
            .insertStartTime(
                lifecycleScope,
                StudyData(0, LocalDateTime.now().toString(), null)
            ) {
                Log.v("startStudy", "The study has been successfully started")
            }

    }

    fun stopStudy() {
        WorkManager.getInstance(this).cancelAllWork()
        UserDataStore
            .getUserRepository(this)
            .insertStartTime(
                lifecycleScope,
                StudyData(0, null, LocalDateTime.now().toString())
            ) {
                Log.v("stopStudy", "The study has been successfully ended")
            }
    }
}