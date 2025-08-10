package com.example.fridgebuddy.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.repository.DispensaRepository
import com.example.fridgebuddy.ui.screens.home.HomeScreen
import com.example.fridgebuddy.ui.screens.list.ListScreen
import com.example.fridgebuddy.ui.screens.list.ListViewModel
import com.example.fridgebuddy.ui.screens.list.add.AddIngredientScreen
import com.example.fridgebuddy.ui.screens.profile.ProfileScreen
import com.example.fridgebuddy.ui.screens.profile.menu.EditProfileScreen
import com.example.fridgebuddy.ui.screens.profile.menu.HelpSupportScreen
import com.example.fridgebuddy.ui.screens.profile.menu.NotificationsScreen
import com.example.fridgebuddy.ui.screens.profile.menu.PrivacySecurityScreen
import com.example.fridgebuddy.ui.screens.recipe.RecipeScreen
import com.example.fridgebuddy.ui.screens.recipe.details.DetailsScreen
import com.example.fridgebuddy.ui.screens.sign.SignInScreen
import com.example.fridgebuddy.ui.screens.sign.SignUpScreen
import com.example.fridgebuddy.ui.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URLDecoder

sealed interface FridgeBuddyRoute {
    @Serializable data object Splash : FridgeBuddyRoute
    @Serializable data object SignIn : FridgeBuddyRoute
    @Serializable data object SignUp : FridgeBuddyRoute
    @Serializable data object Home : FridgeBuddyRoute
    @Serializable data object List : FridgeBuddyRoute
    @Serializable data object AddIngredient : FridgeBuddyRoute
    @Serializable data object Recipe : FridgeBuddyRoute
    @Serializable data class Details(val recipeJson: String) : FridgeBuddyRoute
    @Serializable data object Profile : FridgeBuddyRoute
    @Serializable data object Edit : FridgeBuddyRoute
    @Serializable data object Notifications : FridgeBuddyRoute
    @Serializable data object Privacy : FridgeBuddyRoute
    @Serializable data object Help : FridgeBuddyRoute
}

// Main Navigation Graph
@Composable
fun FridgeBuddyNavGraph(navController: NavHostController) {
    val database = remember { FirebaseDatabase.getInstance() }
    val dispensaRepository = remember { DispensaRepository(database) }
    val auth = remember { FirebaseAuth.getInstance() }
    val listViewModel = remember { ListViewModel(dispensaRepository, auth) }

    NavHost(
        navController = navController,
        startDestination = FridgeBuddyRoute.Splash
    ) {
        // Splash Screen
        composable<FridgeBuddyRoute.Splash> {
            SplashScreen(navController)
        }

        // Sign In Screen
        composable<FridgeBuddyRoute.SignIn> {
            SignInScreen(navController)
        }

        // Sign Up Screen
        composable<FridgeBuddyRoute.SignUp> {
            SignUpScreen(navController)
        }

        // Home Screen
        composable<FridgeBuddyRoute.Home> {
            HomeScreen(navController = navController)
        }

        // List Screen
        composable<FridgeBuddyRoute.List> {
            ListScreen(
                navController = navController,
                viewModel = listViewModel
            )
        }

        composable<FridgeBuddyRoute.AddIngredient> {
            AddIngredientScreen(navController)
        }

        // Recipe Screen
        composable<FridgeBuddyRoute.Recipe> {
            val uiState by listViewModel.uiState.collectAsState()

            RecipeScreen(
                navController = navController,
                dispensaItems = uiState.items
            )
        }

        // Recipe Detail Screen
        composable<FridgeBuddyRoute.Details> { backStackEntry ->
            val recipeJson = backStackEntry.arguments?.getString("recipeJson") ?: ""
            val decodedJson = URLDecoder.decode(recipeJson, "UTF-8")
            val recipe = remember {
                try {
                    Json.decodeFromString<Recipe>(decodedJson)
                } catch (e: Exception) {
                    null
                }
            }

            recipe?.let {
                DetailsScreen(
                    recipe = it,
                    navController = navController
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.navigateUp()
                }
            }
        }

        // Profile Screen
        composable<FridgeBuddyRoute.Profile> {
            ProfileScreen(navController)
        }

        // Edit profile Screen
        composable<FridgeBuddyRoute.Edit> {
            EditProfileScreen(navController)
        }

        // Notifications Screen
        composable<FridgeBuddyRoute.Notifications> {
            NotificationsScreen(navController)
        }

        // Privacy Screen
        composable<FridgeBuddyRoute.Privacy> {
            PrivacySecurityScreen(navController)
        }

        // Help Screen
        composable<FridgeBuddyRoute.Help> {
            HelpSupportScreen(navController)
        }
    }
}

// Main App Navigation Wrapper
@Composable
fun FridgeBuddyNavigation(
    navController: NavHostController = rememberNavController()
) {
    FridgeBuddyNavGraph(navController = navController)
}