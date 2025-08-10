package com.example.fridgebuddy.ui.composables


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgebuddy.R

@Composable
fun DetailsTopBar(
    recipeName: String,
    navController: NavController? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 25.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button con stesso stile
        Surface(
            onClick = { navController?.navigateUp() },
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.goback),
                    contentDescription = "Go back",
                    modifier = Modifier
                        .size(40.dp)
                        .scale(-1f,1f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = recipeName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
        )
    }
}