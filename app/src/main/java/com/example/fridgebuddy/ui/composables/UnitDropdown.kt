package com.example.fridgebuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgebuddy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf("") }
    val units = listOf("kg", "g", "L", "mL", "pz")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedUnit.ifBlank { "" },
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = "Unit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.weight),
                    contentDescription = "Unit icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 20.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .height(60.dp),
            shape = RoundedCornerShape(60),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF06D6A0),
                unfocusedBorderColor = Color(0xFF06D6A0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = unit,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        selectedUnit = unit
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}