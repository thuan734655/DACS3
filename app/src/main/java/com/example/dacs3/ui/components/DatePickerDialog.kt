package com.example.dacs3.ui.components

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

/**
 * Helper function to show a DatePickerDialog
 */
fun DatePickerDialog(
    context: Context,
    initialDate: Date = Date(),
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val resultCalendar = Calendar.getInstance()
            resultCalendar.set(year, month, dayOfMonth)
            onDateSelected(resultCalendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnCancelListener { onDismiss() }
        show()
    }
}