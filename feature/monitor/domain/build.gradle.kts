plugins {
    id("signalmonitor.kotlin.jvm")
    id("signalmonitor.unit.test")
}

dependencies {
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlinx.coroutines.core)
}
