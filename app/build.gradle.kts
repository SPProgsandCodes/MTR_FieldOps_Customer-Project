plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.mtr.fieldopscust"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mtr.fieldopscust"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "15.0"

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
    android {
        buildFeatures {
            viewBinding = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.tools.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.material:material:1.12.0")

    implementation("androidx.vectordrawable:vectordrawable:1.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    //RETROFIT
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    //RX
    implementation ("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation ("com.android.support:multidex:1.0.0")
    implementation ("androidx.activity:activity-ktx:1.9.1")
    implementation ("androidx.fragment:fragment-ktx:1.8.2")

    // For Country code spinner
    implementation ("com.hbb20:ccp:2.5.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("androidx.fragment:fragment-ktx:1.3.6") // or the latest version

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1") // or the latest version

    implementation ("com.googlecode.libphonenumber:libphonenumber:8.12.51")

    //strip sdk
    implementation ("com.stripe:stripe-android:20.19.0")

    implementation ("com.android.volley:volley:1.2.1")

}