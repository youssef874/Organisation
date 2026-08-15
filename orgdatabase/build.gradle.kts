plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.orgdatabase"
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
    implementation(project(":orglogger"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.roboletric)
    testImplementation(libs.mokito)
    testImplementation(libs.room.test)
    testImplementation(libs.androidx.room)
    testImplementation(libs.kotlinx.coroutines.test)
}
