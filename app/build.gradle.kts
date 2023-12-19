plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}


android {
    namespace = "com.demo.weatherforecast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.demo.weatherforecast"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Coroutines
    val coroutinesVer = "1.7.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVer")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVer")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    //Material-3
    implementation("com.google.android.material:material:1.11.0")

    //Dagger - Hilt
    val daggerVer = "2.48.1"
    implementation("com.google.dagger:hilt-android:$daggerVer")
    kapt("com.google.dagger:hilt-android-compiler:$daggerVer")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    //Unit Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Core Testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Optional -- Mockito framework
    val mockitoVersion = "5.7.0"
    testImplementation("org.mockito:mockito-core:$mockitoVersion")

    // Optional -- mockito-kotlin
    val mockitoKotlinVersion = "5.1.0"
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    // Optional -- Mockk framework
    val mockkVersion = "1.13.8"
    testImplementation("io.mockk:mockk:$mockkVersion")
}