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
import androidx.compose.runtime.collectAsState
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
import com.example.fridgebuddy.ui.screens.list.add.AddIngredientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorieDropDown(viewModel: AddIngredientViewModel) {
    val categorie by viewModel.categorie.collectAsState()
    val selectedCategoria by viewModel.selectedCategoria.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategoria.ifBlank { "" },
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = "Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.category),
                    contentDescription = "Category icon",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 20.dp)
                        .scale(scaleX = -1f, scaleY = 1f)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(start = 25.dp, end = 25.dp, bottom = 20.dp)
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
            categorie.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.nome,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    onClick = {
                        viewModel.onCategoriaSelected(category.nome)
                        expanded = false
                    }
                )
            }
        }
    }
}