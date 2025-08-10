package com.example.fridgebuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgebuddy.R

@Composable
fun SearchBar(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                text = "Whatcha lookin' for?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
            )
        },
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Research icon",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(35.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Image(
                        painter = painterResource(id = R.drawable.x),
                        contentDescription = "Delete icon",
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(35.dp)
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(25.dp),
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
}