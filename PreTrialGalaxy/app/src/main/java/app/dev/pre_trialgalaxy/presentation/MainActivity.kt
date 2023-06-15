package app.dev.pre_trialgalaxy.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import app.dev.pre_trialgalaxy.R
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import app.dev.pre_trialgalaxy.presentation.database.entities.AffectData
import app.dev.pre_trialgalaxy.presentation.database.entities.InteractionMeasurement
import app.dev.pre_trialgalaxy.presentation.theme.PreTrialGalaxyTheme
import java.time.LocalDateTime

/*
This class contains the main UI containing the navigation, design and logging of interaction
timestamps.
 */
class MainActivity : ComponentActivity() {

    /*
    The ColorManagers is used to centrally manage the colors used in the Chips and Buttons.
     */
    private val colorManager = ColorManager()

    /*
     These five Arrays contain the affect-descriptors for and within each sector of the A-V-Model. For this
     study the selection for each quadrant was translated to german, due to the study being held
     and participated by germans.
     */
    private val quadrantDescriptors = arrayOf(
        "Verärgert",
        "Traurig",
        "Glücklich",
        "Entspannt"
    )
    private val firstQuadrantWords = arrayOf(
        "Angespannt",
        "Frustriert",
        "Betrübt",
        "Wütend"
    )
    private val secondQuadrantWords = arrayOf(
        "Glücklich",
        "Aufgeregt",
        "Begeistert",
        "Erfreut"
    )
    private val thirdQuadrantWords = arrayOf(
        "Miserabel",
        "Traurig",
        "Deprimiert",
        "Gelangweilt"
    )
    private val fourthQuadrantWords = arrayOf(
        "Schläfrig",
        "Erfüllt",
        "Zufrieden",
        "Ruhig"
    )

    /*
    Initialized Parameters that will be later overwritten with the data collected. If the data
    collection failed, these values would be logged.
     */
    private var notificationTimeId: Long = -1
    private var engagementTime: String = "No time."
    private var completionTime: String = "No time."

    /*
    As soon as the activity is called by the user interacting with the notification, the timestamp
    marking the beginning of the interaction is taken as well as the timestamp when the notification
    was clicked.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        engagementTime = LocalDateTime.now().toString()
        notificationTimeId = intent.getLongExtra("NotificationTimeId", -1)
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost()
        }
    }

    /*
    The AppNavHost provides all the navigation endpoints.
    */
    @Composable
    fun AppNavHost(
        navController: NavHostController = rememberSwipeDismissableNavController(),
        startDestination: String = "start"
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(startDestination) {
                QuadrantQuestion(
                    onNavigateToFirstQuestion = { navController.navigate("first_quadrant") },
                    onNavigateToSecondQuestion = { navController.navigate("second_quadrant") },
                    onNavigateToThirdQuestion = { navController.navigate("third_quadrant") },
                    onNavigateToFourthQuestion = { navController.navigate("fourth_quadrant") }
                )
            }
            composable("first_quadrant") {
                FirstQuadrantQuestion(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }
            composable("second_quadrant") {
                SecondQuadrantQuestion(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }
            composable("third_quadrant") {
                ThirdQuadrantQuestion(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }
            composable("fourth_quadrant") {
                FourthQuadrantQuestion(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }

            composable("confirmation") {
                ConfirmationScreen()
            }
        }
    }

    /*
    This is the first screen that is reached after clicking the notification. It provides the
    broadest overview of the A-V-Model and the sectors.
     */
    @Composable
    fun QuadrantQuestion(
        onNavigateToFirstQuestion: () -> Unit,
        onNavigateToSecondQuestion: () -> Unit,
        onNavigateToThirdQuestion: () -> Unit,
        onNavigateToFourthQuestion: () -> Unit
    ) {
        PreTrialGalaxyTheme {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = onNavigateToFirstQuestion,
                        icon = {
                            Text(
                                text = quadrantDescriptors[0],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = colorManager.getNegativeChipColor()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = onNavigateToThirdQuestion,
                        icon = {
                            Text(
                                text = quadrantDescriptors[1],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = colorManager.getPassiveChipColor()
                    )
                }
                Column {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = onNavigateToSecondQuestion,
                        icon = {
                            Text(
                                text = quadrantDescriptors[2],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = colorManager.getPositiveChipColor()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = onNavigateToFourthQuestion,
                        icon = {
                            Text(
                                text = quadrantDescriptors[3],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                                color = Color.Black
                            )
                        },
                        colors = colorManager.getActiveChipColor()
                    )
                }
            }
        }
    }

    /*
    The next four functions are the separate screens for each quadrants. The spacings between the
    Chips is optimized for the round screen of the Samsung Galaxy Wear 5 Pro.
     */
    // TODO Set fixed widths to the Chips to fit all the descriptors uniformly
    @Composable
    fun FirstQuadrantQuestion(onNavigateToConfirmation: () -> Unit) {
        val color = colorManager.getNegativeChipColor()
        PreTrialGalaxyTheme {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, firstQuadrantWords[0])
                            onNavigateToConfirmation()
                        } ,
                        icon = {
                            Text(
                                text = firstQuadrantWords[0],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, firstQuadrantWords[1])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = firstQuadrantWords[1],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = color
                    )
                }
                Column {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, firstQuadrantWords[2])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = firstQuadrantWords[2],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, firstQuadrantWords[3])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = firstQuadrantWords[3],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                            )
                        },
                        colors = color
                    )
                }
            }
        }
    }

    @Composable
    fun SecondQuadrantQuestion(onNavigateToConfirmation: () -> Unit) {
        val color = colorManager.getPositiveChipColor()
        PreTrialGalaxyTheme {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, secondQuadrantWords[0])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = secondQuadrantWords[0],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, secondQuadrantWords[1])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = secondQuadrantWords[1],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = color
                    )
                }
                Column() {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, secondQuadrantWords[2])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = secondQuadrantWords[2],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, secondQuadrantWords[3])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = secondQuadrantWords[3],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                            )
                        },
                        colors = color
                    )
                }
            }
        }
    }

    @Composable
    fun ThirdQuadrantQuestion(onNavigateToConfirmation: () -> Unit) {
        val color = colorManager.getPassiveChipColor()
        PreTrialGalaxyTheme {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, thirdQuadrantWords[0])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = thirdQuadrantWords[0],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, thirdQuadrantWords[1])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = thirdQuadrantWords[1],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = color
                    )
                }
                Column() {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, thirdQuadrantWords[2])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = thirdQuadrantWords[2],
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, thirdQuadrantWords[3])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = thirdQuadrantWords[3],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                            )
                        },
                        colors = color
                    )
                }
            }
        }
    }

    @Composable
    fun FourthQuadrantQuestion(onNavigateToConfirmation: () -> Unit) {
        val color = colorManager.getActiveChipColor()
        PreTrialGalaxyTheme {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, fourthQuadrantWords[0])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = fourthQuadrantWords[0],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                                color = Color.Black
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, fourthQuadrantWords[1])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = fourthQuadrantWords[1],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                                color = Color.Black
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = color
                    )
                }
                Column() {
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, fourthQuadrantWords[2])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = fourthQuadrantWords[2],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                                color = Color.Black
                            )
                        },
                        colors = color
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Chip(
                        label = {},
                        onClick = {
                            insertAffect(notificationTimeId, fourthQuadrantWords[3])
                            onNavigateToConfirmation()
                        },
                        icon = {
                            Text(
                                text = fourthQuadrantWords[3],
                                modifier = Modifier.wrapContentSize(Alignment.Center),
                                color = Color.Black
                            )
                        },
                        colors = color
                    )
                }
            }
        }
    }

    /*
    The final screen to act as a confirmation that all the user inputs have been processed and
    saved.
    At this point the interaction has concluded and the end-timestamp is taken.
     */
    @Composable
    fun ConfirmationScreen() {
        completionTime = LocalDateTime.now().toString()
        var confirmationShowDialog by remember { mutableStateOf(true) }
        PreTrialGalaxyTheme {
            Dialog(
                showDialog = confirmationShowDialog,
                onDismissRequest = {
                    insertInteraction()
                    confirmationShowDialog = false
                    finish()
                }
            ) {
                Confirmation(
                    onTimeout = {
                        insertInteraction()
                        confirmationShowDialog = false
                        finish()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.confirmation_dialog_tick),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    durationMillis = 1500
                ) {
                    Text(
                        text = stringResource(R.string.success),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    private fun insertAffect(id: Long, affect: String) {
        UserDataStore
            .getUserRepository(applicationContext)
            .insertAffect(lifecycleScope,
                AffectData(0, id, affect)) {
                Log.v("success", "This actually worked")
            }
    }


    private fun insertInteraction() {
        UserDataStore
            .getUserRepository(applicationContext)
            .insertInteractionMeasurement(lifecycleScope,
                InteractionMeasurement(0, notificationTimeId, engagementTime, completionTime)) {
                Log.v("interaction_measurement", "Interaction Time saved.")
            }
    }
}
