plugins {
    id("signalmonitor.android.library")
    id("signalmonitor.android.compose")
}

android {
    namespace = "com.vadimg778.signalmonitor.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
}
