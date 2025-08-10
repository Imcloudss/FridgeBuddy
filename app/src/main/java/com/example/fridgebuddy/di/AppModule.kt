package com.example.fridgebuddy.di

import com.example.fridgebuddy.database.repository.AuthRepository
import com.example.fridgebuddy.database.repository.CategorieRepository
import com.example.fridgebuddy.database.repository.DispensaRepository
import com.example.fridgebuddy.database.repository.FoodRepository
import com.example.fridgebuddy.database.repository.RecipeRepository
import com.example.fridgebuddy.database.repository.UserRepository
import com.example.fridgebuddy.ui.screens.home.HomeViewModel
import com.example.fridgebuddy.ui.screens.list.ListViewModel
import com.example.fridgebuddy.ui.screens.list.add.AddIngredientViewModel
import com.example.fridgebuddy.ui.screens.profile.ProfileViewModel
import com.example.fridgebuddy.ui.screens.sign.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
}

val databaseModule = module {
    single<FirebaseDatabase> {
        FirebaseDatabase.getInstance("https://fridgebuddy-71b54-default-rtdb.firebaseio.com/").apply { }
    }
}

val repositoryModule = module {
    single { AuthRepository(get(), get()) }
    single { CategorieRepository(get()) }
    single<DispensaRepository> { DispensaRepository(get()) }
    single { FoodRepository(get()) }
    single { UserRepository(get()) }
    single { RecipeRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ListViewModel(get(), get()) }
    viewModel { AddIngredientViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel() }
}

val appModules = listOf(
    firebaseModule,
    repositoryModule,
    viewModelModule,
    databaseModule
)