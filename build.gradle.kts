// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false // NUOVO
    alias(libs.plugins.ksp) apply false // NUOVO per Room (KSP)

    id("com.google.gms.google-services") version "4.4.3" apply false
}