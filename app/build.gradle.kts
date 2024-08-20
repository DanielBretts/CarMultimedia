import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.carmultimedia"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.carmultimedia"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        android.buildFeatures.buildConfig=true
        //load the values from .properties file
        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val googleApiKey = properties.getProperty("GOOGLE_API_KEY") ?: ""
        val weatherApiKey = properties.getProperty("WEATHER_API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "GOOGLE_API_KEY",
            value = googleApiKey
        )
        buildConfigField(
            type = "String",
            name = "WEATHER_API_KEY",
            value = weatherApiKey
        )
    }

    viewBinding{
        enable = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.databinding.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.glide)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.7.1")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.google.android.libraries.places:places:3.5.0")
    implementation (libs.play.services.location.v2101)
}