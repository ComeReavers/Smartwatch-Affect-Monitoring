package app.dev.pre_trialgalaxy.presentation

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.*
import androidx.work.*
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import app.dev.pre_trialgalaxy.presentation.database.entities.StudyData
import app.dev.pre_trialgalaxy.presentation.theme.PreTrialGalaxyTheme
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/*
The StudyStartActivity is meant as an admin-panel for the conductors of the study to start and stop
the collection of data and sending of notifications. It is access through the app-icon (whereas the
MainActivity is only accessed using the notification).
 */

class StudyStartActivity : ComponentActivity() {

    /*
    The permissions necessary to run the data collection and the notifications are listed here.
     */
    private val requestedPermissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WAKE_LOCK
    )

    /*
    The promptFrequency used by the periodicWorker to prompt the notification. The minimum required
    by the Worker is 15 minutes.

    NOTE: This is not an exact time when the notification gets sent! After the time set the work
    only gets scheduled and execution depends on the WearOS scheduler.
     */
    private val promptFrequency = 15L
    private val promptFrequencyTimeUnit = TimeUnit.MINUTES

    /*
    The initial time until the periodicWorker is first called is set here. A initial delay of one
    minute was chosen to accustom the study participants to the UI and the functionality right after
    they first put on the smartwatch and the study was started.
     */
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

    /*
    This defines the screen to start and stop the study and the collection of data.

    Note: It was found that during the in-the-field study, the visual feedback on button presses
    were not enough to be sure the study has started. A colored background or a screen like the
    ConfirmationScreen in MainActivity could be used to signal the status of the study and changes
    in it.
     */
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

    /*
    The initialization of the study. This cancels of stops every action as well to make multiple
    presses not start multiple instances. Also the starting timestamp of the study is logged.
     */
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

    /*
    The study is ended by stopping the Work manager and collecting the timestamp of the end.
     */
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