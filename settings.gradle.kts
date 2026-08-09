import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.authentication.http.HttpHeaderAuthentication
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// ===========================================================================
// LATIHAN VIDA - helper credential, salinan dari app/build.gradle.kts.
//
// Kenapa disalin dan tidak dipakai bersama: settings.gradle.kts dievaluasi
// SEBELUM build script proyek mana pun, jadi tidak bisa memanggil fungsi yang
// didefinisikan di app/build.gradle.kts. Di project kantor blok maven ada di
// build script sehingga bisa memakai helper yang sama; di repo ini tidak bisa,
// lihat catatan FAIL_ON_PROJECT_REPOS di bawah.
// ===========================================================================
val localProps = Properties()
val localPropsFile = file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { localProps.load(it) }
}

// Prioritas: env var (CD) -> local.properties (dev) -> kosong
//
// === GROOVY (seperti di project kantor) ===
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // -------------------------------------------------------------------
        // Maven repo SDK, dengan header x-api-key.
        //
        // CATATAN STRUKTUR: di project kantor blok ini ada di build script
        // modul. Di repo ini TIDAK BISA, karena repositoriesMode di atas
        // disetel FAIL_ON_PROJECT_REPOS - Gradle akan menolak repository yang
        // dideklarasikan di level proyek. Perilaku yang dilatih tetap identik.
        //
        // JEBAKAN YANG SENGAJA DIPERTAHANKAN: isProdBuild hanyalah heuristik
        // substring pada nama task.
        //   ./gradlew assembleProdRelease  -> ada "prod" -> URL prod     BENAR
        //   ./gradlew build                -> tidak ada  -> URL sandbox  SALAH,
        //                                     padahal ikut meng-compile flavor
        //                                     prod. Resolusi artifact prod akan
        //                                     mencari ke server sandbox.
        //
        // DIBUKTIKAN 2026-08-09 lewat dua echo server di port berbeda:
        //   ./gradlew :app:assembleRelease  -> APK prod terbangun, tapi server
        //   prod menerima NOL request. Semua artifact ditarik dari sandbox.
        //
        // Dan heuristiknya lebih longgar dari yang terlihat: startParameter
        // .taskNames ikut memuat ARGUMEN task, bukan cuma nama task. Terbukti
        // pada task yang sama persis:
        //   :app:dependencies --configuration prodReleaseCompileClasspath -> prod
        //   :app:dependencies --configuration sitReleaseCompileClasspath  -> sandbox
        // Jadi kata "prod" yang nyasar di argumen mana pun ikut menyalakannya.
        // -------------------------------------------------------------------
        maven {
            val isProdBuild = gradle.startParameter.taskNames.any {
                it.lowercase().contains("prod")
            }

            // URL dan group diambil dari local.properties (atau env var di CD),
            // BUKAN ditulis di sini - hostname internal perusahaan tidak boleh
            // ikut ter-commit, repo ini PUBLIC. Fallback-nya echo server lokal
            // supaya repo tetap bisa dibangun tanpa akses jaringan VIDA.
            val sandboxUrl = vidaSecret("VIDA_REPO_URL_SANDBOX", "vida.repoUrlSandbox")
                .ifEmpty { "http://127.0.0.1:8081/maven" }
            val prodUrl = vidaSecret("VIDA_REPO_URL_PROD", "vida.repoUrlProd")
                .ifEmpty { "http://127.0.0.1:8082/maven" }
            val sdkGroup = vidaSecret("VIDA_REPO_GROUP", "vida.repoGroup")
                .ifEmpty { "com.vida.rehearsal" }

            name = "vidaSdkRepo"
            url = uri(if (isProdBuild) prodUrl else sandboxUrl)

            // Hanya berpengaruh untuk URL http polos, yaitu echo server latihan.
            // URL VIDA asli https, jadi baris ini tidak melonggarkan apa pun
            // untuknya. Di project kantor baris ini TIDAK ada.
            isAllowInsecureProtocol = true

            credentials(HttpHeaderCredentials::class) {
                name = "x-api-key"
                // Kedua argumen ini NAMA, bukan nilai: yang pertama nama env
                // var (dipakai CD), yang kedua nama properti di
                // local.properties (dipakai dev). Menaruh credential asli di
                // baris ini akan membocorkannya ke repo public - dan tetap
                // tidak berfungsi, karena yang dicari getenv() adalah namanya.
                value = vidaSecret("VIDA_REPO_KEY", "vida.repoKey")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }

            // Kunci repo ini hanya untuk group SDK. Tanpa ini, setiap dependency
            // yang tidak ketemu di google()/mavenCentral() akan ikut menembak
            // repo VIDA - artinya header x-api-key terkirim untuk artifact yang
            // tidak ada hubungannya, dan build gagal saat repo tak terjangkau.
            content {
                includeGroup(sdkGroup)
            }
        }
    }
}

rootProject.name = "MyAlquran"
include(":app")
