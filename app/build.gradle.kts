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

// Prioritas: env var (CI) -> local.properties (dev) -> kosong
//
// === GROOVY (project kantor) ===
// def vidaSecret = { String envKey, String localKey ->
//     def fromEnv = System.getenv(envKey)
//     if (fromEnv != null && !fromEnv.isEmpty()) return fromEnv
//     def fromLocal = localProps.getProperty(localKey)
//     if (fromLocal != null && !fromLocal.isEmpty()) return fromLocal
//     return ""
// }
fun vidaSecret(envKey: String, localKey: String): String {
    val fromEnv = System.getenv(envKey)
    if (!fromEnv.isNullOrEmpty()) return fromEnv
    val fromLocal = localProps.getProperty(localKey)
    if (!fromLocal.isNullOrEmpty()) return fromLocal
    return ""
}

// Fail-fast: dipanggil dari tiap flavor.
//
// === GROOVY (project kantor) ===
// def requireVidaSecret = { String envKey, String localKey, String flavorName ->
//     def value = vidaSecret(envKey, localKey)
//     if (value.isEmpty()) {
//         throw new GradleException(
//                 "VIDA secret '${envKey}' kosong untuk flavor '${flavorName}'. " +
//                         "Set env var ${envKey} (CI) atau properti ${localKey} di local.properties (dev)."
//         )
//     }
//     return value
// }
fun requireVidaSecret(envKey: String, localKey: String, flavorName: String): String {
    val value = vidaSecret(envKey, localKey)
    if (value.isEmpty()) {
        throw GradleException(
            "VIDA secret '$envKey' kosong untuk flavor '$flavorName'. " +
                    "Set env var $envKey (CI) atau properti $localKey di local.properties (dev)."
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
            // buildConfigField 'String', 'API_KEY_VIDA',
            //     "\"${requireVidaSecret('VIDA_API_KEY', 'vida.apiKey', 'sit')}\""
            buildConfigField(
                "String", "API_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_API_KEY", "vida.apiKey", "sit")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_LICENSE_KEY", "vida.licenseKey", "sit")}\""
            )

            // === GROOVY (project kantor) ===
            // manifestPlaceholders = [ vidaActivationKey: requireVidaSecret('VIDA_ACTIVATION_KEY', 'vida.activationKey', 'sit') ]
            // Kotlin DSL tidak menerima bentuk `= [ ... ]`; pakai indexing.
            manifestPlaceholders["vidaActivationKey"] =
                requireVidaSecret("VIDA_ACTIVATION_KEY", "vida.activationKey", "sit")
        }

        // Tiga flavor berikut bentuknya sama persis, hanya berbeda nama flavor
        // pada argumen ketiga - argumen itu semata untuk pesan error fail-fast.
        create("uat") {
            dimension = "environment"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat"
            buildConfigField(
                "String", "API_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_API_KEY", "vida.apiKey", "uat")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_LICENSE_KEY", "vida.licenseKey", "uat")}\""
            )
            manifestPlaceholders["vidaActivationKey"] =
                requireVidaSecret("VIDA_ACTIVATION_KEY", "vida.activationKey", "uat")
        }

        create("mock") {
            dimension = "environment"
            applicationIdSuffix = ".mock"
            versionNameSuffix = "-mock"
            buildConfigField(
                "String", "API_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_API_KEY", "vida.apiKey", "mock")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_LICENSE_KEY", "vida.licenseKey", "mock")}\""
            )
            manifestPlaceholders["vidaActivationKey"] =
                requireVidaSecret("VIDA_ACTIVATION_KEY", "vida.activationKey", "mock")
        }

        create("prod") {
            dimension = "environment"
            // prod tidak memakai suffix - applicationId-nya yang asli.
            buildConfigField(
                "String", "API_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_API_KEY", "vida.apiKey", "prod")}\""
            )
            buildConfigField(
                "String", "LICENSE_KEY_VIDA",
                "\"${requireVidaSecret("VIDA_LICENSE_KEY", "vida.licenseKey", "prod")}\""
            )
            manifestPlaceholders["vidaActivationKey"] =
                requireVidaSecret("VIDA_ACTIVATION_KEY", "vida.activationKey", "prod")
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

    implementation("org.jsoup:jsoup:1.14.3")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // SDK VIDA ASLI. Ditarik dari maven repo ber-header x-api-key yang
    // dikonfigurasi di settings.gradle.kts. Credential dan URL-nya datang dari
    // local.properties (dev) atau env var SDK_* / VIDA_* (CD) - tidak pernah
    // dari file ini, karena repo ini PUBLIC.
    //
    // ARTIFACT BERBEDA PER FLAVOR. sit/uat/mock memakai varian `-sandbox`,
    // prod TIDAK. Ini yang bikin `implementation(...)` polos berbahaya: satu
    // baris untuk semua flavor berarti build prod diam-diam membawa SDK
    // sandbox, dan tidak ada gejala apa pun saat build - beda dengan jebakan
    // isProdBuild di settings.gradle.kts yang setidaknya gagal resolusi.
    //
    // AGP membuat konfigurasi <flavor>Implementation untuk tiap flavor. Di
    // Kotlin DSL namanya dipanggil sebagai string karena dibuat dinamis.
    val vidaVersion = "1.9.1"
    "sitImplementation"("id.vida:liveness-sandbox:$vidaVersion")
    "uatImplementation"("id.vida:liveness-sandbox:$vidaVersion")
    "mockImplementation"("id.vida:liveness-sandbox:$vidaVersion")

    // prod SENGAJA BELUM DIAKTIFKAN.
    // Alasan: credential yang tersedia baru untuk sandbox, dan nama artifact
    // produksinya belum dipastikan - `liveness` di bawah masih tebakan yang
    // belum pernah diverifikasi ke sdk-repo.vida.id.
    //
    // Selama baris ini mati, build prod TETAP JALAN tapi TANPA SDK VIDA sama
    // sekali. Aman untuk sekarang karena belum ada kode yang memanggil kelas
    // SDK-nya - tapi begitu ada, prod akan gagal compile, bukan diam-diam
    // salah. Aktifkan setelah credential prod ada DAN nama artifact-nya
    // dikonfirmasi ke VIDA.
    // "prodImplementation"("id.vida:liveness:$vidaVersion")

}