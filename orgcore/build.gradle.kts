plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.orgcore"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":orgdatabase"))
    implementation(project(":orglogger"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.data.store.core)
    implementation(libs.data.store.preference.core)
    implementation(libs.data.store.preference)
    implementation(libs.android.startup)
    testImplementation(libs.junit)
    testImplementation(libs.roboletric)
    testImplementation(libs.mokito)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}