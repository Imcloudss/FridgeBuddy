package com.example.fridgebuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fridgebuddy.R
import com.example.fridgebuddy.database.model.FoodItem
import com.example.fridgebuddy.ui.screens.list.add.AddIngredientViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchImg(
    viewModel: AddIngredientViewModel = koinViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val suggestions by viewModel.filteredFoodItems.collectAsState()
    var selectedItem by remember { mutableStateOf<FoodItem?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.onSearchChanged(it)
                if (it.isEmpty()) selectedItem = null // se cancelli, resetti la selezione
            },
            placeholder = {
                Text(
                    text = "Search ingredient's image",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.pic),
                    modifier = Modifier
                        .size(50.dp)
                        .padding(start = 20.dp)
                        .scale(scaleX = -1f, scaleY = 1f),
                    contentDescription = "Pic icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 8.dp)
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

        // Show suggestions
        if (suggestions.isNotEmpty() && selectedItem == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .heightIn(max = 200.dp)
            ) {
                items(suggestions.take(5)) { item ->
                    SuggestionCard(
                        foodItem = item,
                        onClick = {
                            selectedItem = item
                            viewModel.onFoodItemSelected(item)
                        }
                    )
                }
            }
        }

        // Selected image's box
        selectedItem?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = item.imgUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(2.dp, Color(0xFF06D6A0), RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
fun SuggestionCard(
    foodItem: FoodItem,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(60))
            .background(Color.White)
            .border(1.dp, Color(0xFF06D6A0), shape = RoundedCornerShape(60))
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = foodItem.imgUrl,
            contentDescription = foodItem.title,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(50)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = foodItem.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
