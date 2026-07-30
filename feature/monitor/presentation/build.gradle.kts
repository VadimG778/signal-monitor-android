plugins {
    id("signalmonitor.android.library")
    id("signalmonitor.android.compose")
    id("signalmonitor.kotlin.serialization")
    id("signalmonitor.scichart.license")
    id("signalmonitor.unit.test")
}

android {
    namespace = "com.vadimg778.signalmonitor.feature.monitor.presentation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:monitor:domain"))

    implementation(platform(libs.koin.bom))

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.koin.core.viewmodel)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)
    implementation(libs.scichart.charting)
    implementation(libs.scichart.core)
    implementation(libs.scichart.data)
    implementation(libs.scichart.drawing)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.orbit.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.activity.compose)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
