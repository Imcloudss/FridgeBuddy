package com.example.fridgebuddy.ui.screens.list.add

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.composables.CategorieDropDown
import com.example.fridgebuddy.ui.composables.DatePickerField
import com.example.fridgebuddy.ui.composables.SearchImg
import com.example.fridgebuddy.ui.composables.UnitDropdown
import com.example.fridgebuddy.ui.screens.home.HomeViewModel
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddIngredientScreen(
    navController: NavController
) {
    var nameIngredient by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val context = LocalContext.current
    val viewModel: AddIngredientViewModel = koinViewModel()
    val homeViewModel: HomeViewModel = koinViewModel()
    val selectedFood = viewModel.selectedFoodItem.collectAsState().value
    var selectedDate by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("gr") }
    val addResult by viewModel.addResult.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(addResult) {
        addResult?.let {
            if (it.isSuccess) {
                Toast.makeText(context, "Ingredient added!", Toast.LENGTH_SHORT).show()
                homeViewModel.refreshData()
                navController.navigate(FridgeBuddyRoute.List)
            } else {
                Toast.makeText(context, "Error: ${it.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(selectedFood) {
        selectedFood?.let {
            nameIngredient = it.title
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FFE5)),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 70.dp
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.market),
                contentDescription = "Pantry icon",
                modifier = Modifier
                    .size(85.dp)
                    .padding(end = 15.dp)
            )

            Text(
                text = "Add ingredient",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color(0xFF06D6A0),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 170.dp)
        ) {
            OutlinedTextField(
                value = nameIngredient,
                onValueChange = { nameIngredient = it },
                placeholder = {
                    Text(
                        text = "Ingredient name",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.newfood),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp)
                            .scale(
                                scaleX = -1f,
                                scaleY = 1f
                            ),
                        contentDescription = "New food icon"
                    )
                },
                modifier = Modifier
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
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.dp,
                        bottom = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    placeholder = {
                        Text(
                            text = "Quantity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.quantity),
                            modifier = Modifier
                                .size(50.dp)
                                .padding(start = 20.dp),
                            contentDescription = "Quantity icon"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.85f)
                        .padding(start = 25.dp)
                        .height(60.dp),
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

                Box(modifier = Modifier.weight(0.95f)) {
                    UnitDropdown(
                        onUnitSelected = { selectedUnit = it }
                    )
                }
            }

            DatePickerField(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            CategorieDropDown(viewModel)

            SearchImg()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 15.dp),
                thickness = 1.dp,
                color = Color(0xFF06D6A0)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 40.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 25.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Add button
                ExtendedFloatingActionButton(
                    onClick = {
                        val newItem = viewModel.createDispensaItem(
                            nomeIngrediente = nameIngredient,
                            quantita = quantity.toIntOrNull() ?: 0,
                            unita = selectedUnit,
                            dataScadenza = selectedDate
                        )

                        newItem?.let {
                            viewModel.addDispensaItem(userId, it)
                        } ?: run {
                            Toast.makeText(context, "Select an ingredient", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color(0xFF06D6A0),
                    contentColor = Color.White,
                    modifier = Modifier
                        .width(165.dp)
                        .height(80.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shop),
                        contentDescription = "Add",
                        modifier = Modifier
                            .size(50.dp)
                            .scale(
                                scaleX = -1f,
                                scaleY = 1f
                            )
                    )
                    Text(
                        text = "Add",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Cancel button
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate(FridgeBuddyRoute.List)
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF06D6A0),
                    modifier = Modifier
                        .width(165.dp)
                        .height(80.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "Cancel",
                        modifier = Modifier
                            .size(50.dp)
                            .scale(
                                scaleX = -1f,
                                scaleY = 1f
                            )
                    )
                    Text(
                        text = "Cancel",
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}