import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

// ===========================================================================
// LATIHAN VIDA - helper injeksi credential SDK saat build.
//
// Titik pentingnya: env var MENANG atas local.properties, sehingga runner CD
// otomatis menimpa nilai dev tanpa konfigurasi tambahan.
//
// Blok Groovy di komentar adalah bentuk asli di project kantor. Repo ini
// memakai Kotlin DSL, jadi sintaksnya berbeda walau perilakunya sama.
// ===========================================================================

// === GROOVY (project kantor) ===
// def localProps = new Properties()
// def localPropsFile = rootProject.file('local.properties')
// if (localPropsFile.exists()) {
//     localPropsFile.withInputStream { localProps.load(it) }
// }
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { localProps.load(it) }
}

// Prioritas: env var (CD) -> local.properties (dev) -> kosong
//
// === GROOVY (project kantor) ===
// def sdkSecret = { String envKey, String localKey ->
//     def fromEnv = System.getenv(envKey)
//     if (fromEnv != null && !fromEnv.isEmpty()) return fromEnv
//     def fromLocal = localProps.getProperty(localKey)
//     if (fromLocal != null && !fromLocal.isEmpty()) return fromLocal
//     return ""
// }
fun sdkSecret(envKey: String, localKey: String): String {
    val fromEnv = System.getenv(envKey)
    if (!fromEnv.isNullOrEmpty()) return fromEnv
    val fromLocal = localProps.getProperty(localKey)
    if (!fromLocal.isNullOrEmpty()) return fromLocal
    return ""
}

// Fail-fast: build gagal kalau credential kosong.
//
// === GROOVY (project kantor) ===
// def requireSdkSecret = { String envKey, String localKey, String flavorName ->
//     def value = sdkSecret(envKey, localKey)
//     if (value.isEmpty()) {
//         throw new GradleException(
//             "Secret '${envKey}' kosong untuk flavor '${flavorName}'. " +
//             "Set env var ${envKey} (CD) atau properti ${localKey} di local.properties (dev)."
//         )
//     }
//     return value
// }
fun requireSdkSecret(envKey: String, localKey: String, flavorName: String): String {
    val value = sdkSecret(envKey, localKey)
    if (value.isEmpty()) {
        throw GradleException(
            "Secret '$envKey' kosong untuk flavor '$flavorName'. " +
                "Set env var $envKey (CD) atau properti $localKey di local.properties (dev)."
        )
    }
    return value
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

    // -----------------------------------------------------------------------
    // Flavor + injeksi credential.
    //
    // ESCAPED-QUOTE ITU WAJIB. buildConfigField menuliskan nilainya sebagai
    // literal Java apa adanya:
    //     "\"${...}\""  ->  public static final String API_KEY_SDK = "test-123";
    //          tanpa     ->  public static final String API_KEY_SDK = test-123;
    // Yang kedua tidak compile. Ini kesalahan yang paling sering terjadi.
    //
    // requireSdkSecret dipanggil saat CONFIGURATION, bukan saat task jalan.
    // Jadi blok keempat flavor dievaluasi di setiap build - satu credential
    // kosong membuat semua build gagal, termasuk build flavor lain. Itu
    // memang perilaku fail-fast yang diinginkan.
    // -----------------------------------------------------------------------
    flavorDimensions += "environment"

    productFlavors {
        create("sit") {
            dimension = "environment"
            applicationIdSuffix = ".sit"
            versionNameSuffix = "-sit"

            // === GROOVY (project kantor) ===
            // buildConfigField 'String', 'API_KEY_SDK',
            //     "\"${requireSdkSecret('SDK_API_KEY', 'sdk.apiKey', 'sit')}\""
            buildConfigField(
                "String", "API_KEY_SDK",
                "\"${requireSdkSecret("SDK_API_KEY", "sdk.apiKey", "sit")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_SDK",
                "\"${requireSdkSecret("SDK_LICENSE_KEY", "sdk.licenseKey", "sit")}\""
            )

            // === GROOVY (project kantor) ===
            // manifestPlaceholders = [
            //     sdkActivationKey: requireSdkSecret('SDK_ACTIVATION_KEY', 'sdk.activationKey', 'sit')
            // ]
            // Kotlin DSL tidak menerima bentuk `= [ ... ]`; pakai indexing.
            manifestPlaceholders["sdkActivationKey"] =
                requireSdkSecret("SDK_ACTIVATION_KEY", "sdk.activationKey", "sit")
        }

        // Tiga flavor berikut bentuknya sama persis, hanya berbeda nama flavor
        // pada argumen ketiga - argumen itu semata untuk pesan error fail-fast.
        create("uat") {
            dimension = "environment"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat"
            buildConfigField(
                "String", "API_KEY_SDK",
                "\"${requireSdkSecret("SDK_API_KEY", "sdk.apiKey", "uat")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_SDK",
                "\"${requireSdkSecret("SDK_LICENSE_KEY", "sdk.licenseKey", "uat")}\""
            )
            manifestPlaceholders["sdkActivationKey"] =
                requireSdkSecret("SDK_ACTIVATION_KEY", "sdk.activationKey", "uat")
        }

        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"
            buildConfigField(
                "String", "API_KEY_SDK",
                "\"${requireSdkSecret("SDK_API_KEY", "sdk.apiKey", "mock")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_SDK",
                "\"${requireSdkSecret("SDK_LICENSE_KEY", "sdk.licenseKey", "mock")}\""
            )
            manifestPlaceholders["sdkActivationKey"] =
                requireSdkSecret("SDK_ACTIVATION_KEY", "sdk.activationKey", "mock")
        }

        create("prod") {
            dimension = "environment"
            // prod tidak memakai suffix - applicationId-nya yang asli.
            buildConfigField(
                "String", "API_KEY_SDK",
                "\"${requireSdkSecret("SDK_API_KEY", "sdk.apiKey", "prod")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_SDK",
                "\"${requireSdkSecret("SDK_LICENSE_KEY", "sdk.licenseKey", "prod")}\""
            )
            manifestPlaceholders["sdkActivationKey"] =
                requireSdkSecret("SDK_ACTIVATION_KEY", "sdk.activationKey", "prod")
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
        // AGP 8 mematikan buildConfig secara default. Tanpa baris ini,
        // buildConfigField di bawah tidak menghasilkan BuildConfig sama sekali
        // dan build gagal dengan error yang menyesatkan.
        buildConfig = true
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

        implementation("com.github.bumptech.glide:glide:4.15.1")

        implementation ("org.jsoup:jsoup:1.14.3")

        implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

        // Stub SDK - HANYA untuk membuktikan header x-api-key benar-benar
        // terkirim ke maven repo. Ini bukan SDK VIDA asli dan tidak menarik
        // artifact asli apa pun.
        //
        // Sengaja di balik flag supaya build sehari-hari tidak bergantung pada
        // echo server yang harus hidup:
        //     ./gradlew :app:assembleSitRelease -PwithSdkStub
        if (providers.gradleProperty("withSdkStub").isPresent) {
            implementation("com.vida.rehearsal:vida-sdk-stub:1.0.0")
        }
    }