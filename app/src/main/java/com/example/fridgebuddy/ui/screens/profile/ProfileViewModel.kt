package com.example.fridgebuddy.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.model.User
import com.example.fridgebuddy.database.repository.RecipeRepository
import com.example.fridgebuddy.database.repository.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val errorMessage: String? = null,
    val savedRecipes: List<Recipe> = emptyList(),
    val ingredientsSaved: Int = 0,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val settingsState: SettingsState = SettingsState(),
    val updateSuccess: Boolean = false,
    val showLogoutDialog: Boolean = false
)

data class NotificationSettings(
    val expiryEnabled: Boolean = false,
    val expiryDays: Int = 3,
    val recipeEnabled: Boolean = false,
    val shoppingEnabled: Boolean = false
)

data class SettingsState(
    val twoFactorEnabled: Boolean = false,
    val loginAlertsEnabled: Boolean = true,
    val dataSharing: Boolean = false,
    val theme: String = "light",
    val language: String = "en"
)

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val recipeRepository: RecipeRepository? = null
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadUserData()
        loadNotificationSettings()
        loadDispensaItemsCount()
        loadSavedRecipes()
        loadUserSettings()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = auth.currentUser?.uid
            if (userId != null) {
                Log.d(TAG, "Loading user data for: $userId")

                userRepository.getUser(userId).collect { user ->
                    Log.d(TAG, "User data received: ${user?.username}")
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } else {
                Log.w(TAG, "No authenticated user found")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User not authenticated"
                    )
                }
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingImage = true, errorMessage = null) }

            try {
                auth.currentUser?.uid?.let { userId ->
                    Log.d(TAG, "Uploading profile image for user: $userId")

                    val storageRef = storage.reference
                        .child("profile_images")
                        .child("$userId.jpg")

                    val uploadTask = storageRef.putFile(imageUri).await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await()

                    userRepository.updateProfileImage(userId, downloadUrl.toString())
                        .onSuccess {
                            Log.d(TAG, "Profile image updated successfully")
                            _uiState.update { currentState ->
                                currentState.copy(
                                    user = currentState.user?.copy(img = downloadUrl.toString()),
                                    isUploadingImage = false,
                                    updateSuccess = true
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Failed to update profile image", error)
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Failed to upload image: ${error.message}",
                                    isUploadingImage = false
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Error uploading image: ${e.message}",
                        isUploadingImage = false
                    )
                }
            }
        }
    }

    fun updateUserProfile(username: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                auth.currentUser?.uid?.let { userId ->
                    val currentUser = _uiState.value.user
                    val currentAuthEmail = auth.currentUser?.email

                    val updatedUser = currentUser?.copy(
                        username = username,
                        email = email
                    ) ?: User(
                        id = userId,
                        username = username,
                        email = email
                    )

                    userRepository.updateUser(updatedUser)
                        .onSuccess {
                            // Check if email needs to be updated in Firebase Auth
                            if (currentAuthEmail != email) {
                                try {
                                    // Use the new method that sends verification email
                                    auth.currentUser?.verifyBeforeUpdateEmail(email)?.await()

                                    Log.d(TAG, "Email verification sent to: $email")
                                    _uiState.update {
                                        it.copy(
                                            user = updatedUser,
                                            isLoading = false,
                                            updateSuccess = true,
                                            errorMessage = "Verification email sent to $email. Please check your inbox."
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to send email verification", e)
                                    // Still update the username even if email update failed
                                    _uiState.update {
                                        it.copy(
                                            user = updatedUser.copy(email = currentAuthEmail ?: email),
                                            isLoading = false,
                                            updateSuccess = true,
                                            errorMessage = "Username updated. Email update requires re-authentication."
                                        )
                                    }
                                }
                            } else {
                                // Only username changed
                                _uiState.update {
                                    it.copy(
                                        user = updatedUser,
                                        isLoading = false,
                                        updateSuccess = true
                                    )
                                }
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Failed to update profile", error)
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Failed to update profile: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating profile", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Error updating profile: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadDispensaItemsCount() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    val dispensaRef = database.getReference("utenti/$userId/dispensa")
                    val snapshot = dispensaRef.get().await()

                    val count = snapshot.childrenCount.toInt()
                    Log.d(TAG, "Dispensa items count: $count")

                    _uiState.update { it.copy(ingredientsSaved = count) }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading dispensa count", e)
                }
            }
        }
    }

    private fun loadSavedRecipes() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    val recipesRef = database.getReference("utenti/$userId/savedRecipes")
                    val snapshot = recipesRef.get().await()

                    val savedRecipeIds = mutableListOf<Int>()
                    snapshot.children.forEach { child ->
                        child.getValue(Int::class.java)?.let { savedRecipeIds.add(it) }
                    }

                    Log.d(TAG, "Found ${savedRecipeIds.size} saved recipes")

                    recipeRepository?.let { repo ->
                        val recipes = mutableListOf<Recipe>()
                        savedRecipeIds.forEach { id ->
                            repo.getRecipeById(id)
                                .onSuccess { recipe -> recipes.add(recipe) }
                        }
                        _uiState.update { it.copy(savedRecipes = recipes) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading saved recipes", e)
                }
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    val settingsRef = database.getReference("utenti/$userId/notificationSettings")
                    val snapshot = settingsRef.get().await()

                    if (snapshot.exists()) {
                        val settings = NotificationSettings(
                            expiryEnabled = snapshot.child("expiryEnabled").getValue(Boolean::class.java) ?: true,
                            expiryDays = snapshot.child("expiryDays").getValue(Int::class.java) ?: 3,
                            recipeEnabled = snapshot.child("recipeEnabled").getValue(Boolean::class.java) ?: false,
                            shoppingEnabled = snapshot.child("shoppingEnabled").getValue(Boolean::class.java) ?: true
                        )

                        Log.d(TAG, "Notification settings loaded")
                        _uiState.update { it.copy(notificationSettings = settings) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading notification settings", e)
                }
            }
        }
    }

    private fun loadUserSettings() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    val settingsRef = database.getReference("users/$userId/settings")
                    val snapshot = settingsRef.get().await()

                    if (snapshot.exists()) {
                        val settings = SettingsState(
                            twoFactorEnabled = snapshot.child("twoFactorEnabled").getValue(Boolean::class.java) ?: false,
                            loginAlertsEnabled = snapshot.child("loginAlertsEnabled").getValue(Boolean::class.java) ?: true,
                            dataSharing = snapshot.child("dataSharing").getValue(Boolean::class.java) ?: false,
                            theme = snapshot.child("theme").getValue(String::class.java) ?: "light",
                            language = snapshot.child("language").getValue(String::class.java) ?: "en"
                        )

                        Log.d(TAG, "User settings loaded")
                        _uiState.update { it.copy(settingsState = settings) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading user settings", e)
                }
            }
        }
    }

    fun updateNotificationSettings(
        expiryEnabled: Boolean,
        expiryDays: Int,
        recipeEnabled: Boolean,
        shoppingEnabled: Boolean
    ) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    val settings = mapOf(
                        "expiryEnabled" to expiryEnabled,
                        "expiryDays" to expiryDays,
                        "recipeEnabled" to recipeEnabled,
                        "shoppingEnabled" to shoppingEnabled
                    )

                    database.getReference("utenti/$userId/notificationSettings")
                        .setValue(settings)
                        .await()

                    Log.d(TAG, "Notification settings updated")

                    _uiState.update {
                        it.copy(
                            notificationSettings = NotificationSettings(
                                expiryEnabled = expiryEnabled,
                                expiryDays = expiryDays,
                                recipeEnabled = recipeEnabled,
                                shoppingEnabled = shoppingEnabled
                            ),
                            updateSuccess = true
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating notification settings", e)
                    _uiState.update {
                        it.copy(errorMessage = "Failed to update notification settings")
                    }
                }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                user?.email?.let { email ->
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user.reauthenticate(credential).await()
                    user.updatePassword(newPassword).await()

                    Log.d(TAG, "Password changed successfully")

                    _uiState.update {
                        it.copy(updateSuccess = true, errorMessage = null)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error changing password", e)
                _uiState.update {
                    it.copy(errorMessage = "Failed to change password: ${e.message}")
                }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                auth.currentUser?.uid?.let { userId ->
                    Log.d(TAG, "Deleting account for user: $userId")

                    database.getReference("utenti/$userId").removeValue().await()

                    database.getReference("users/$userId").removeValue().await()

                    try {
                        storage.reference.child("profile_images/$userId.jpg").delete().await()
                    } catch (e: Exception) {
                        Log.w(TAG, "No profile image to delete")
                    }

                    auth.currentUser?.delete()?.await()

                    Log.d(TAG, "Account deleted successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting account", e)
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete account: ${e.message}")
                }
            }
        }
    }

    fun updateSettings(settings: Map<String, Any>) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    database.getReference("users/$userId/settings")
                        .updateChildren(settings)
                        .await()

                    Log.d(TAG, "Settings updated: ${settings.keys.joinToString()}")

                    _uiState.update { currentState ->
                        val updatedSettings = currentState.settingsState.copy(
                            twoFactorEnabled = settings["twoFactorEnabled"] as? Boolean ?: currentState.settingsState.twoFactorEnabled,
                            loginAlertsEnabled = settings["loginAlertsEnabled"] as? Boolean ?: currentState.settingsState.loginAlertsEnabled,
                            dataSharing = settings["dataSharing"] as? Boolean ?: currentState.settingsState.dataSharing,
                            theme = settings["theme"] as? String ?: currentState.settingsState.theme,
                            language = settings["language"] as? String ?: currentState.settingsState.language
                        )

                        currentState.copy(
                            settingsState = updatedSettings,
                            updateSuccess = true
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating settings", e)
                    _uiState.update {
                        it.copy(errorMessage = "Failed to update settings")
                    }
                }
            }
        }
    }

    fun incrementCompletedRecipes() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                userRepository.incrementCompletedRecipes(userId)
                    .onSuccess {
                        Log.d(TAG, "Completed recipes incremented")
                        _uiState.update { currentState ->
                            currentState.copy(
                                user = currentState.user?.copy(
                                    completedRecipe = (currentState.user.completedRecipe + 1)
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        Log.e(TAG, "Failed to increment completed recipes", error)
                    }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                Log.d(TAG, "User logged out")
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                _uiState.update {
                    it.copy(errorMessage = "Error during logout: ${e.message}")
                }
            }
        }
    }

    fun showLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun hideLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun showError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearUpdateSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }
}