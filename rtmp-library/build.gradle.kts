import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.example.rtmp_library"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    lint {
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf("-Xskip-metadata-version-check")
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    
    // Uygulamada kullanan kiþinin OpenGlView ekrann grebilmesi iin API (da ak balant) olmal
    api("com.github.pedroSG94.RootEncoder:library:2.6.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.github.ayseelg"
                artifactId = "rtmp-library"
                version = "1.0.1"
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ayseelg/rtmp-live-stream-android")
                credentials {
                    // Cihazdaki local.properties üzerinden okuyacağız (Görünmez kalması için)
                    val localProperties = Properties()
                    val localPropertiesFile = rootProject.file("local.properties")
                    if (localPropertiesFile.exists()) {
                        localProperties.load(FileInputStream(localPropertiesFile))
                    }
                    username = localProperties.getProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
                    password = localProperties.getProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}