plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.kotlin.compose.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.serialization.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "signalmonitor.android.application"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = "signalmonitor.android.compose"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "signalmonitor.android.library"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("kotlinJvm") {
            id = "signalmonitor.kotlin.jvm"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.KotlinJvmConventionPlugin"
        }
        register("kotlinSerialization") {
            id = "signalmonitor.kotlin.serialization"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.KotlinSerializationConventionPlugin"
        }
        register("sciChartLicense") {
            id = "signalmonitor.scichart.license"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.SciChartLicenseConventionPlugin"
        }
        register("unitTest") {
            id = "signalmonitor.unit.test"
            implementationClass = "com.vadimg778.signalmonitor.buildlogic.UnitTestConventionPlugin"
        }
    }
}
