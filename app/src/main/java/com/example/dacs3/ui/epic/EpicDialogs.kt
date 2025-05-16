package com.example.dacs3.ui.epic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dacs3.data.model.Epic
import java.text.SimpleDateFormat
import java.util.*

// String extension function for capitalization
fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

@Composable
fun CreateEpicDialog(
    onDismiss: () -> Unit,
    onCreateEpic: (title: String, description: String?, startDate: Date?, dueDate: Date?, priority: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("TO_DO") }
    var priority by remember { mutableStateOf("Medium") }
    
    // Date picker states
    var startDate by remember { mutableStateOf<Date?>(null) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    
    var titleError by remember { mutableStateOf("") }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Create Epic",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it 
                        titleError = if (it.isBlank()) "Title cannot be empty" else ""
                    },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError.isNotEmpty(),
                    supportingText = { if (titleError.isNotEmpty()) Text(titleError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    var statusExpanded by remember { mutableStateOf(false) }
                    val statusOptions = listOf("TO_DO", "IN_PROGRESS", "DONE")
                    
                    OutlinedTextField(
                        value = status,
                        onValueChange = { },
                        label = { Text("Status") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { statusExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    var priorityExpanded by remember { mutableStateOf(false) }
                    val priorityOptions = listOf("Low", "Medium", "High")
                    
                    OutlinedTextField(
                        value = priority,
                        onValueChange = { },
                        label = { Text("Priority") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { priorityExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false },
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Date pickers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Date: ", 
                        modifier = Modifier.width(80.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = startDate?.let { dateFormat.format(it) } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due Date: ", 
                        modifier = Modifier.width(80.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = { showDueDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = dueDate?.let { dateFormat.format(it) } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = "Title cannot be empty"
                            } else {
                                onCreateEpic(
                                    title,
                                    description.ifBlank { null },
                                    startDate,
                                    dueDate,
                                    priority,
                                    status
                                )
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
    
    // Date pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { date ->
                startDate = date
                showStartDatePicker = false
            },
            initialDate = startDate ?: Date()
        )
    }
    
    if (showDueDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            onDateSelected = { date ->
                dueDate = date
                showDueDatePicker = false
            },
            initialDate = dueDate ?: Date()
        )
    }
}

@Composable
fun UpdateEpicDialog(
    epic: Epic,
    onDismiss: () -> Unit,
    onUpdateEpic: (id: String, title: String, description: String?, assignedTo: String?, 
              status: String, priority: String, startDate: Date?, dueDate: Date?, 
              completedDate: Date?, sprintId: String?) -> Unit
) {
    var title by remember { mutableStateOf(epic.title) }
    var description by remember { mutableStateOf(epic.description ?: "") }
    var status by remember { mutableStateOf(epic.status) }
    var priority by remember { mutableStateOf(epic.priority) }
    
    // Date picker states
    var startDate by remember { mutableStateOf(epic.start_date) }
    var dueDate by remember { mutableStateOf(epic.due_date) }
    var completedDate by remember { mutableStateOf(epic.completed_date) }
    
    // Assigned to is just a string ID in our model
    var assignedTo by remember { mutableStateOf(epic.assigned_to) }
    var sprintId by remember { mutableStateOf(epic.sprint_id) }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    var showCompletedDatePicker by remember { mutableStateOf(false) }
    
    var titleError by remember { mutableStateOf("") }
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Update Epic",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { 
                        title = it 
                        titleError = if (it.isBlank()) "Title cannot be empty" else ""
                    },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = titleError.isNotEmpty(),
                    supportingText = { if (titleError.isNotEmpty()) Text(titleError) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    var statusExpanded by remember { mutableStateOf(false) }
                    val statusOptions = listOf("TO_DO", "IN_PROGRESS", "DONE")
                    
                    OutlinedTextField(
                        value = status,
                        onValueChange = { },
                        label = { Text("Status") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { statusExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Priority dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    var priorityExpanded by remember { mutableStateOf(false) }
                    val priorityOptions = listOf("Low", "Medium", "High")
                    
                    OutlinedTextField(
                        value = priority,
                        onValueChange = { },
                        label = { Text("Priority") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { priorityExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }
                    )
                    
                    DropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false },
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        priorityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    priority = option
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Start Date Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Date: ", 
                        modifier = Modifier.width(100.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = startDate?.let { dateFormat.format(it) } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Due Date Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due Date: ", 
                        modifier = Modifier.width(100.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = { showDueDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = dueDate?.let { dateFormat.format(it) } ?: "Select Date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // Completed Date Picker (only shown if status is 'DONE')
                if (status == "DONE") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completed: ", 
                            modifier = Modifier.width(100.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedButton(
                            onClick = { showCompletedDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = completedDate?.let { dateFormat.format(it) } ?: "Select Date",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = "Title cannot be empty"
                            } else {
                                onUpdateEpic(
                                    epic._id,
                                    title,
                                    description.ifBlank { null },
                                    assignedTo,
                                    status,
                                    priority,
                                    startDate,
                                    dueDate,
                                    completedDate,
                                    sprintId
                                )
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
    
    // Date pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { date ->
                startDate = date
                showStartDatePicker = false
            },
            initialDate = startDate ?: Date()
        )
    }
    
    if (showDueDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            onDateSelected = { date ->
                dueDate = date
                showDueDatePicker = false
            },
            initialDate = dueDate ?: Date()
        )
    }
    
    if (showCompletedDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showCompletedDatePicker = false },
            onDateSelected = { date ->
                completedDate = date
                showCompletedDatePicker = false
            },
            initialDate = completedDate ?: Date()
        )
    }
}

@Composable
fun FilterEpicDialog(
    onDismiss: () -> Unit,
    onApplyFilter: (status: String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Filter Epics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status filter
                Text(
                    text = "Filter by Status:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Status options
                val statusOptions = listOf("ALL", "TO_DO", "IN_PROGRESS", "DONE")
                
                Column(modifier = Modifier.selectableGroup()) {
                    statusOptions.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStatus = if (status == "ALL") null else status
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (status == "ALL" && selectedStatus == null) || 
                                         (status != "ALL" && selectedStatus == status),
                                onClick = {
                                    selectedStatus = if (status == "ALL") null else status
                                }
                            )
                            Text(
                                text = status,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(onClick = { onApplyFilter(selectedStatus) }) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    initialDate: Date
) {
    val calendar = remember { Calendar.getInstance() }
    calendar.time = initialDate
    
    val year = remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val month = remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    val day = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date picker content - simplified for this example
                // Year picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Year:", modifier = Modifier.width(80.dp))
                    OutlinedTextField(
                        value = year.value.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { newYear ->
                                if (newYear in 2000..2100) { // Reasonable range
                                    year.value = newYear
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Month picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Month:", modifier = Modifier.width(80.dp))
                    val monthNames = listOf("January", "February", "March", "April", "May", "June", 
                                         "July", "August", "September", "October", "November", "December")
                    
                    Box(modifier = Modifier.weight(1f)) {
                        var expanded by remember { mutableStateOf(false) }
                        
                        OutlinedTextField(
                            value = monthNames[month.value],
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                                }
                            }
                        )
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(IntrinsicSize.Min)
                        ) {
                            monthNames.forEachIndexed { index, name ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        month.value = index
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Day picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Day:", modifier = Modifier.width(80.dp))
                    OutlinedTextField(
                        value = day.value.toString(),
                        onValueChange = { 
                            it.toIntOrNull()?.let { newDay ->
                                // Get the max days in the selected month
                                calendar.set(Calendar.YEAR, year.value)
                                calendar.set(Calendar.MONTH, month.value)
                                val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                
                                if (newDay in 1..maxDays) {
                                    day.value = newDay
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            calendar.set(year.value, month.value, day.value)
                            onDateSelected(calendar.time)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
