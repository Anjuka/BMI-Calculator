plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    // Add the Google services Gradle plugin
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.angleone.bmical"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.angleone.bmical"
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
        dataBinding = true
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


    //AgentWeb
    implementation(libs.agentweb.core)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation(libs.firebase.messaging.ktx)
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation(libs.blankj.utilcodex)

    //swiperefreshlayout
    // implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")

//    implementation(libs.adjust.android)
//    // Add the following if you are using the Adjust SDK inside web views on your app
//    implementation(libs.adjust.webbridge)

    implementation("com.appsflyer:af-android-sdk:6.14.1")
    implementation(libs.installreferrer)

}