package com.example.fridgebuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    Column {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 15.dp),
            thickness = 1.dp,
            color = Color(0xFF06D6A0))

        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFFF8FFE5)
        ) {
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(FridgeBuddyRoute.Home) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        modifier = Modifier.size(50.dp)
                    )
                },
            )

            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(FridgeBuddyRoute.List) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.list),
                        contentDescription = "List",
                        modifier = Modifier.size(40.dp),
                    )
                }
            )

            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(FridgeBuddyRoute.Recipe) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.receipe),
                        contentDescription = "Recipe",
                        modifier = Modifier.size(40.dp)
                    )
                },
            )

            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(FridgeBuddyRoute.Profile) },
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.chef),
                        contentDescription = "Account",
                        modifier = Modifier
                            .size(40.dp)
                            .scale(-1f, 1f)
                    )
                },
            )
        }
    }
}