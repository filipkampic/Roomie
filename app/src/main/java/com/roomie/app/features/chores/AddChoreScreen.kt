package com.roomie.app.features.chores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.sensors.ShakeDetector
import com.roomie.app.core.ui.components.RoomieButton
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.chores.components.AssignToDropdown
import com.roomie.app.features.chores.components.DeadlineField
import com.roomie.app.core.ui.components.FieldLabel
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.features.chores.components.TimePickerDialog
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChoreScreen(
    navController: NavHostController,
    choreId: String? = null,
    viewModel: ChoreViewModel = hiltViewModel()
) {
    val isEditMode = choreId != null
    val editChore by viewModel.editChore.collectAsState()
    val members by viewModel.members.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var title by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf<Pair<String, String>?>(null) }
    var deadlineMillis by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    var showAssignDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    DisposableEffect(members) {
        val shakeDetector = ShakeDetector {
            if (members.isNotEmpty()) {
                val randomMember = members.random()
                assignedTo = randomMember
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Assigned to ${randomMember.second}!",
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
        sensorManager.registerListener(
            shakeDetector,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    LaunchedEffect(choreId) {
        if (choreId != null) viewModel.loadChore(choreId)
    }

    LaunchedEffect(editChore) {
        val chore = editChore ?: return@LaunchedEffect
        title = chore.title
        notes = chore.notes
        if (chore.deadline > 0L) {
            deadlineMillis = chore.deadline
            selectedDateMillis = chore.deadline
            val cal = Calendar.getInstance().apply { timeInMillis = chore.deadline }
            selectedHour = cal.get(Calendar.HOUR_OF_DAY)
            selectedMinute = cal.get(Calendar.MINUTE)
        }
        assignedTo = members.find { it.first == chore.assignedTo }
    }

    LaunchedEffect(members, editChore) {
        if (isEditMode && assignedTo == null && members.isNotEmpty()) {
            assignedTo = members.find { it.first == editChore?.assignedTo } ?: members.first()
        } else if (!isEditMode && assignedTo == null && members.isNotEmpty()) {
            assignedTo = members.first()
        }
    }

    LaunchedEffect(actionState) {
        if (actionState is ChoreActionState.Success) {
            viewModel.resetActionState()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = TealPrimary,
                    contentColor = SurfaceWhite,
                    actionColor = SurfaceWhite,
                    shape = RoomieShapes.medium
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = if (isEditMode) "Edit Chore" else "New Chore",
                style = RoomieTypography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isEditMode) "Update household task" else "Create a shared household task",
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            RoomieCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.CardPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
                ) {
                    FieldLabel("Task Name")
                    RoomieTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        placeholder = "Take out trash",
                        isError = titleError
                    )
                    if (titleError) {
                        Text(
                            text = "Task name is required",
                            style = RoomieTypography.labelSmall,
                            color = DestructiveRed
                        )
                    }

                    FieldLabel("Assign To")
                    AssignToDropdown(
                        members = members,
                        selected = assignedTo,
                        expanded = showAssignDropdown,
                        onExpandChange = { showAssignDropdown = it },
                        onSelect = {
                            assignedTo = it
                            showAssignDropdown = false
                        }
                    )

                    FieldLabel("Deadline")
                    DeadlineField(
                        deadlineMillis = deadlineMillis,
                        onClick = { showDatePicker = true }
                    )

                    FieldLabel("Notes")
                    RoomieTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Additional task details...",
                        minLines = 4,
                        maxLines = 6
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            RoomieButton(
                text = if (isEditMode) "Update Chore" else "Create Chore",
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@RoomieButton
                    }
                    if (isEditMode) {
                        viewModel.updateChore(
                            choreId = choreId!!,
                            title = title.trim(),
                            assignedTo = assignedTo?.first ?: "",
                            deadline = deadlineMillis ?: 0L,
                            notes = notes.trim()
                        )
                    } else {
                        viewModel.addChore(
                            title = title.trim(),
                            assignedTo = assignedTo?.first ?: "",
                            deadline = deadlineMillis ?: 0L,
                            notes = notes.trim()
                        )
                    }
                },
                isLoading = actionState is ChoreActionState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                    showTimePicker = true
                }) {
                    Text("Next", color = TealPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedDayContainerColor = TealPrimary,
                    selectedDayContentColor = SurfaceWhite,
                    todayDateBorderColor = TealPrimary,
                    todayContentColor = TealPrimary,
                    selectedYearContainerColor = TealPrimary,
                    currentYearContentColor = TealPrimary,
                )
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            is24Hour = true
        )
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedHour = timePickerState.hour
                selectedMinute = timePickerState.minute
                val date = selectedDateMillis ?: System.currentTimeMillis()
                val cal = Calendar.getInstance().apply {
                    timeInMillis = date
                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    set(Calendar.MINUTE, timePickerState.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                deadlineMillis = cal.timeInMillis
                showTimePicker = false
            }
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = SurfaceWhite,
                    clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectorColor = TealPrimary,
                    timeSelectorSelectedContainerColor = TealPrimary,
                    timeSelectorSelectedContentColor = SurfaceWhite
                )
            )
        }
    }
}