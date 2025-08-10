package com.example.fridgebuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgebuddy.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        placeholder = {
            Text(
                text = "Expiring date",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar icon",
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 20.dp)
                    .scale(scaleX = -1f, scaleY = 1f),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.padding(end = 15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open calendar"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 25.dp, bottom = 12.dp)
            .height(60.dp)
            .clickable { showDatePicker = true },
        shape = RoundedCornerShape(60),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF06D6A0),
            unfocusedBorderColor = Color(0xFF06D6A0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = formatter.format(Date(millis))
                            onDateSelected(formattedDate)
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF06D6A0))
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF06D6A0))
                ) {
                    Text("Cancel")
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color(0xFFF8FFE5))
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFFF8FFE5),
                    titleContentColor = Color(0xFF02CC99),
                    headlineContentColor = Color(0xFF02CC99),
                    weekdayContentColor = Color(0xFF02CC99),
                    subheadContentColor = Color(0xFF02CC99),
                    navigationContentColor = Color(0xFF02CC99),
                    yearContentColor = Color(0xFF02CC99),
                    disabledYearContentColor = Color(0xFF02CC99).copy(alpha = 0.38f),
                    currentYearContentColor = Color(0xFF02CC99),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF02CC99),
                    dayContentColor = Color(0xFF02CC99),
                    disabledDayContentColor = Color(0xFF02CC99).copy(alpha = 0.38f),
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF02CC99),
                    todayContentColor = Color(0xFF02CC99),
                    todayDateBorderColor = Color(0xFF02CC99)
                )
            )
        }
    }
}
