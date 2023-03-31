package app.dev.pre_trialgalaxy.presentation

import android.Manifest
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.health.services.client.HealthServices
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.room.Room
import androidx.wear.compose.material.*
import androidx.wear.compose.material.dialog.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import app.dev.pre_trialgalaxy.R
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import app.dev.pre_trialgalaxy.presentation.database.UserDatabase
import app.dev.pre_trialgalaxy.presentation.database.entities.AffectData
import app.dev.pre_trialgalaxy.presentation.database.entities.InteractionMeasurement
import app.dev.pre_trialgalaxy.presentation.theme.PreTrialGalaxyTheme
import java.time.LocalDateTime
import kotlin.system.exitProcess

//import com.samsung.android.eventsmonitor.EventBroadcastReceiver

class MainActivity : ComponentActivity() {

    private val colorManager = ColorManager()

    //TODO think about how to differentiate the smartwatches and databases the produce
    private val databaseId : String = ""


    //TODO set constant width for chips
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

    private var notificationTimeId: Long = -1
    private var engagementTime: String = "No time."
    private var completionTime: String = "No time."

    override fun onCreate(savedInstanceState: Bundle?) {
        engagementTime = LocalDateTime.now().toString()
        notificationTimeId = intent.getLongExtra("NotificationTimeId", -1)
        //HealthManager(applicationContext).startHeartRateMeasurements()
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost()
        }
    }

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


            // First Prototype
            composable("first_prototype") {
                FirstPrototypeArousal(onNavigateToFirstValence = { navController.navigate("first_valence") })
            }
            composable("first_valence") {
                FirstPrototypeValence(onNavigateToPicker = { navController.navigate("picker") })
            }

            // Second Prototype
            composable("second_prototype") {
                SecondPrototypeArousal(onNavigateToSecondPrototypeValence = {
                    navController.navigate(
                        "second_valence"
                    )
                })
            }
            composable("second_valence") {
                SecondPrototypeValence(onNavigateToPicker = { navController.navigate("picker") })
            }

            // Third Prototype
            composable("third_prototype") {
                ThirdPrototypeArousal(onNavigateToThirdValence = { navController.navigate("third_valence") })
            }

            composable("third_valence") {
                ThirdPrototypeValence(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }

            // Fourth Prototype
            composable("fourth_prototype") {
                ForthPrototype(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }


            // REVISED PROTOTYPES START HERE
            // Fifth Prototype
            composable("question_fifth") {
                QuestionScreenEmoji(onNavigateToFourthPrototypeRevised = { navController.navigate("fifth_prototype") })
            }

            //composable("fifth_prototype") {
            //    QuadrantQuestion(onNavigateToConfirmation = { navController.navigate("confirmation") })
            //}

            // First Prototype Revised
            composable("question_arousal") {
                QuestionScreenArousal(onNavigateToFirstPrototypeRevised = { navController.navigate("first_prototype_revised") })
            }

            composable("first_prototype_revised") {
                FirstPrototypeArousalRevised(onNavigateToFirstValenceRevised = {
                    navController.navigate(
                        "question_valence"
                    )
                })
            }

            composable("question_valence") {
                QuestionScreenValence(onNavigateToFirstValenceRevised = { navController.navigate("first_valence_revised") })
            }

            composable("first_valence_revised") {
                FirstPrototypeValenceRevised(onNavigateToPicker = { navController.navigate("picker") })
            }

            // Fourth Prototype Revised
            composable("question_emoji") {
                QuestionScreenEmoji(onNavigateToFourthPrototypeRevised = { navController.navigate("fourth_prototype_revised") })
            }
            composable("fourth_prototype_revised") {
                ForthPrototypeRevised(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }


            //FINAL NAVIGATION TREE
            composable("quadrant_question") {
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


            composable("picker") {
                MyPicker(onNavigateToConfirmation = { navController.navigate("confirmation") })
            }

            composable("confirmation") {
                ConfirmationScreen()
            }
        }
    }

    @Composable
    fun WearApp(
        onNavigateToFirstPrototypeRevised: () -> Unit,
        onNavigateToFourthPrototypeRevised: () -> Unit,
        onNavigateToFifthPrototype: () -> Unit
    ) {
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                autoCentering = AutoCenteringParams(itemIndex = 1),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ListHeader {
                        Text(
                            text = "Select the UI Prototype"
                        )
                    }
                }

                item {
                    Chip(
                        label = {
                            Text(text = "First Prototype")
                        },
                        onClick = {}
                    )
                }

                item {
                    Chip(
                        label = {
                            Text(text = "Fourth Prototype")
                        },
                        onClick = {}
                    )
                }

                item {
                    Chip(
                        label = {
                            Text(text = "Fifth Prototype")
                        },
                        onClick = {}
                    )
                }
            }
        }
    }

    @Composable
    fun FirstPrototypeArousal(onNavigateToFirstValence: () -> Unit) {
        var stepperValue by remember { mutableStateOf(0) }
        val minProgression = -5
        val maxProgression = 5
        val progression = minProgression..maxProgression
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                valueProgression = progression,
                decreaseIcon = { Icon(StepperDefaults.Decrease, "Decrease") },
                increaseIcon = { Icon(StepperDefaults.Increase, "Increase") }
            )
            {
                Chip(
                    label = {
                        Text(
                            text = stringResource(R.string.arousal_question),
                            textAlign = TextAlign.Center
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "Weiter",
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = onNavigateToFirstValence,
                    modifier = Modifier
                        .width(175.dp)
                        .fillMaxHeight()
                )
            }
            CircularProgressIndicator(
                progress = (0.5 + (0.5F / maxProgression.toFloat()) * stepperValue).toFloat(),
                modifier = Modifier.fillMaxSize(),
                startAngle = (90 / maxProgression) * (stepperValue.toFloat() * -1),
                strokeWidth = 15.dp,
                indicatorColor = colorManager.getPositiveArousalCircularColor()
            )
        }
    }

    @Composable
    fun FirstPrototypeValence(onNavigateToPicker: () -> Unit) {
        var stepperValue by remember { mutableStateOf(0) }
        val minProgression = -5
        val maxProgression = 5
        val progression = minProgression..maxProgression
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                valueProgression = progression,
                decreaseIcon = { Icon(StepperDefaults.Decrease, "Decrease") },
                increaseIcon = { Icon(StepperDefaults.Increase, "Increase") }
            ) {
                Chip(
                    label = {
                        Text(
                            text = stringResource(R.string.valence_question),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "Weiter",
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = onNavigateToPicker,
                    modifier = Modifier
                        .width(175.dp)
                        .fillMaxHeight()
                )
            }
            CircularProgressIndicator(
                progress = (0.5 + (0.5F / maxProgression.toFloat()) * stepperValue).toFloat(),
                modifier = Modifier.fillMaxSize(),
                startAngle = (90 / maxProgression) * (stepperValue.toFloat() * -1),
                strokeWidth = 15.dp,
                indicatorColor = colorManager.getPassiveValenceCircularColor()
            )
        }
    }

    @Composable
    fun SecondPrototypeArousal(onNavigateToSecondPrototypeValence: () -> Unit) {
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                autoCentering = AutoCenteringParams(itemIndex = 1, itemOffset = 1)
            ) {
                item {
                    Text(text = "Wie fühlst du dich?")
                }
                item {
                    Chip(
                        label = { Text(text = "Gut") },
                        onClick = onNavigateToSecondPrototypeValence,
                        colors = colorManager.getPositiveChipColor()
                    )
                }
                item {
                    Chip(
                        label = { Text(text = "Schlecht") },
                        onClick = onNavigateToSecondPrototypeValence,
                        colors = colorManager.getNegativeChipColor()
                    )
                }
            }
        }
    }

    @Composable
    fun SecondPrototypeValence(onNavigateToPicker: () -> Unit) {
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                autoCentering = AutoCenteringParams(itemIndex = 1)
            ) {
                item {
                    Text(text = "Wie aktiv fühlst du dich?")
                }
                item {
                    Chip(
                        label = { Text(text = "Aktiv") },
                        onClick = onNavigateToPicker,
                        colors = colorManager.getActiveChipColor()
                    )
                }
                item {
                    Chip(
                        label = { Text(text = "Passiv") },
                        onClick = onNavigateToPicker,
                        colors = colorManager.getPassiveChipColor()
                    )
                }
            }
        }
    }


    //Pickers
    @Composable
    fun ThirdPrototypeArousal(onNavigateToThirdValence: () -> Unit) {
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            val arousalValues = listOf(3, 2, 1, 0, -1, -2, -3)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Picker(
                    state = PickerState(
                        initialNumberOfOptions = arousalValues.size,
                        initiallySelectedOption = arousalValues.size / 2,
                        repeatItems = false
                    ),
                    modifier = Modifier.size(150.dp, 150.dp),
                    contentDescription = "Picker for Arousal"
                ) {
                    Text(text = arousalValues[it].toString(), fontSize = 40.sp)

                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )

                Button(
                    onClick = onNavigateToThirdValence,
                    colors = colorManager.getPositiveArousalButtonColor()
                ) {
                    Text(text = "Weiter")
                }
            }
        }
    }

    @Composable
    fun ThirdPrototypeValence(onNavigateToConfirmation: () -> Unit) {
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            val arousalValues = listOf(3, 2, 1, 0, -1, -2, -3)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Picker(
                    state = PickerState(
                        initialNumberOfOptions = arousalValues.size,
                        initiallySelectedOption = arousalValues.size / 2,
                        repeatItems = false
                    ),
                    modifier = Modifier.size(150.dp, 150.dp),
                    contentDescription = "Picker for Valence"//,

                ) {
                    Text(
                        text = arousalValues[it].toString(),
                        fontSize = 40.sp
                    )
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f)
                )

                Button(
                    onClick = onNavigateToConfirmation,
                    colors = colorManager.getPassiveValenceButtonColor()
                ) {
                    Text(text = "Weiter")
                }
            }
        }
    }

    //Emoji
    @Composable
    fun ForthPrototype(onNavigateToConfirmation: () -> Unit) {
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                autoCentering = AutoCenteringParams(itemIndex = 2),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ListHeader {
                        Text(
                            text = "Wie fühlst du dich gerade?",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.confused
                                    ),
                                    contentDescription = "Confused Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.crying
                                    ),
                                    contentDescription = "Crying Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.furious
                                    ),
                                    contentDescription = "Furious Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.happy
                                    ),
                                    contentDescription = "Happy Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.laughing
                                    ),
                                    contentDescription = "Laughing Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.sad
                                    ),
                                    contentDescription = "Sad Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FirstPrototypeArousalRevised(onNavigateToFirstValenceRevised: () -> Unit) {
        var stepperValue by remember { mutableStateOf(0) }
        val minProgression = -3
        val maxProgression = 3
        val progression = minProgression..maxProgression
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                valueProgression = progression,
                decreaseIcon = { Icon(StepperDefaults.Decrease, "Decrease") },
                increaseIcon = { Icon(StepperDefaults.Increase, "Increase") }
            )
            {
                Chip(
                    label = {
                        Text(
                            text = "Weiter",
                            textAlign = TextAlign.Center
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_forward_ios_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = onNavigateToFirstValenceRevised,
                    modifier = Modifier
                        .width(175.dp)
                        .fillMaxHeight()
                )
            }
            CircularProgressIndicator(
                progress = (0.5 + (0.5F / maxProgression.toFloat()) * stepperValue).toFloat(),
                modifier = Modifier.fillMaxSize(),
                startAngle = (90 / maxProgression) * (stepperValue.toFloat() * -1),
                strokeWidth = 15.dp,
                indicatorColor = colorManager.getPositiveArousalCircularColor()
            )
        }
    }

    @Composable
    fun FirstPrototypeValenceRevised(onNavigateToPicker: () -> Unit) {
        var stepperValue by remember { mutableStateOf(0) }
        val minProgression = -3
        val maxProgression = 3
        val progression = minProgression..maxProgression
        val colorManager = ColorManager()
        PreTrialGalaxyTheme {
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                valueProgression = progression,
                decreaseIcon = { Icon(StepperDefaults.Decrease, "Decrease") },
                increaseIcon = { Icon(StepperDefaults.Increase, "Increase") }
            ) {
                Chip(
                    label = {
                        Text(
                            text = "Weiter",
                            textAlign = TextAlign.Center
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_forward_ios_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = onNavigateToPicker,
                    modifier = Modifier
                        .width(175.dp)
                        .fillMaxHeight()
                )
            }
            CircularProgressIndicator(
                progress = (0.5 + (0.5F / maxProgression.toFloat()) * stepperValue).toFloat(),
                modifier = Modifier.fillMaxSize(),
                startAngle = (90 / maxProgression) * (stepperValue.toFloat() * -1),
                strokeWidth = 15.dp,
                indicatorColor = colorManager.getPassiveValenceCircularColor()
            )
        }
    }

    @Composable
    fun ForthPrototypeRevised(onNavigateToConfirmation: () -> Unit) {
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                autoCentering = AutoCenteringParams(itemIndex = 1),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.happy
                                    ),
                                    contentDescription = "Happy Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.laughing
                                    ),
                                    contentDescription = "Laughing Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.annoyed
                                    ),
                                    contentDescription = "Annoyed Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.confused
                                    ),
                                    contentDescription = "Confused Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.sad
                                    ),
                                    contentDescription = "Sad Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )

                        Spacer(modifier = Modifier.width(width = 5.dp))

                        Chip(
                            label = {},
                            onClick = onNavigateToConfirmation,
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.tense
                                    ),
                                    contentDescription = "Tense Emoji",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentSize(align = Alignment.Center),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }
                }
            }
        }
    }

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
                                text = "Verärgert",
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
                                text = "Traurig",
                                modifier = Modifier.wrapContentSize(Alignment.Center)
                            )
                        },
                        modifier = Modifier.width(100.dp),
                        colors = colorManager.getPassiveChipColor()
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
                        onClick = onNavigateToSecondQuestion,
                        icon = {
                            Text(
                                text = "Glücklich",
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
                                text = "Entspannt",
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

    private fun insertAffect(id: Long, affect: String) {
        UserDataStore
            .getUserRepository(applicationContext)
            .insertAffect(lifecycleScope,
                AffectData(0, id, affect)) {
                Log.v("success", "This actually worked")
            }
    }

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

    @Composable
    fun QuestionScreenEmoji(onNavigateToFourthPrototypeRevised: () -> Unit) {
        var confirmationShowDialog by remember { mutableStateOf(true) }
        PreTrialGalaxyTheme {
            Dialog(
                showDialog = confirmationShowDialog,
                onDismissRequest = {
                    confirmationShowDialog = false
                    onNavigateToFourthPrototypeRevised()
                }
            ) {
                Confirmation(
                    onTimeout = {
                        confirmationShowDialog = false
                        onNavigateToFourthPrototypeRevised()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.question_mark),
                            contentDescription = stringResource(R.string.emoji_question),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    durationMillis = 1500
                ) {
                    Text(
                        text = stringResource(R.string.emoji_question),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun QuestionScreenArousal(onNavigateToFirstPrototypeRevised: () -> Unit) {
        var confirmationShowDialog by remember { mutableStateOf(true) }
        PreTrialGalaxyTheme {
            Dialog(
                showDialog = confirmationShowDialog,
                onDismissRequest = {
                    confirmationShowDialog = false
                    onNavigateToFirstPrototypeRevised()
                }
            ) {
                Confirmation(
                    onTimeout = {
                        confirmationShowDialog = false
                        onNavigateToFirstPrototypeRevised()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.question_mark),
                            contentDescription = stringResource(R.string.arousal_question),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    durationMillis = 1500
                ) {
                    Text(
                        text = stringResource(R.string.arousal_question),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun QuestionScreenValence(onNavigateToFirstValenceRevised: () -> Unit) {
        var confirmationShowDialog by remember { mutableStateOf(true) }
        PreTrialGalaxyTheme {
            Dialog(
                showDialog = confirmationShowDialog,
                onDismissRequest = {
                    confirmationShowDialog = false
                    onNavigateToFirstValenceRevised()
                }
            ) {
                Confirmation(
                    onTimeout = {
                        confirmationShowDialog = false
                        onNavigateToFirstValenceRevised()
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.question_mark),
                            contentDescription = stringResource(R.string.valence_question),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    durationMillis = 1500
                ) {
                    Text(
                        text = stringResource(R.string.valence_question),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun MyPicker(onNavigateToConfirmation: () -> Unit) {
        PreTrialGalaxyTheme {
            ScalingLazyColumn(
                autoCentering = AutoCenteringParams(itemIndex = 0),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Welches Wort beschreibt deine Gefühle am besten?",
                        textAlign = TextAlign.Center
                    )
                }
                item {
                    Chip(
                        label = { Text(text = "Euphorisch") },
                        onClick = onNavigateToConfirmation
                    )
                }
                item {
                    Chip(
                        label = { Text(text = "Glücklich") },
                        onClick = onNavigateToConfirmation
                    )
                }
                item {
                    Chip(
                        label = { Text(text = "Zufrieden") },
                        onClick = onNavigateToConfirmation
                    )
                }
            }
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

    @Composable
    fun ConfirmationScreen() {
        completionTime = LocalDateTime.now().toString()
        var confirmationShowDialog by remember { mutableStateOf(true) }
        PreTrialGalaxyTheme {
            Dialog(
                showDialog = confirmationShowDialog,
                onDismissRequest = {
                    insertInteraction()
                    //HealthManager(applicationContext).stopHeartRateMeasurements()
                    confirmationShowDialog = false
                    finish()
                }
            ) {
                Confirmation(
                    onTimeout = {
                        insertInteraction()
                        //HealthManager(applicationContext).stopHeartRateMeasurements()
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
}
