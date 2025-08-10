package com.example.fridgebuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.fridgebuddy.di.appModules
import com.example.fridgebuddy.ui.theme.FridgeBuddyNavigation
import com.example.fridgebuddy.ui.theme.FridgeBuddyTheme
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Firebase initialization
        FirebaseApp.initializeApp(this)

        // Koin initialization
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger(Level.ERROR)
                androidContext(this@MainActivity)
                modules(appModules)
            }
        }

        setContent {
            KoinAndroidContext {
                FridgeBuddyTheme {
                    val navController = rememberNavController()
                    FridgeBuddyNavigation(navController)
                }
            }
        }
    }
}