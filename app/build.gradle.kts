import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.expert.myalquran"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.expert.myalquran"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        val roomVersion = "2.6.1"
        implementation("androidx.room:room-ktx:$roomVersion")
        implementation("androidx.room:room-runtime:$roomVersion")
        ksp("androidx.room:room-compiler:$roomVersion")
        androidTestImplementation("androidx.room:room-testing:$roomVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

        //DI
        val koin_version = "3.3.2"
        implementation("io.insert-koin:koin-core:$koin_version")
        implementation("io.insert-koin:koin-android:$koin_version")

        //API
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
        implementation("com.squareup.okhttp3:okhttp:4.10.0")

        // circle image
        implementation("de.hdodenhof:circleimageview:3.1.0")

        // lottie
        implementation("com.airbnb.android:lottie:4.2.0")


        implementation ("org.jsoup:jsoup:1.14.3")

        implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    }