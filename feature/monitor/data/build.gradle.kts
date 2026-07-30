plugins {
    id("signalmonitor.android.library")
    id("signalmonitor.unit.test")
}

android {
    namespace = "com.vadimg778.signalmonitor.feature.monitor.data"
}

dependencies {
    implementation(project(":feature:monitor:domain"))
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.coroutines.core)
}
