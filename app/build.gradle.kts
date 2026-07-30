plugins {
    id("signalmonitor.android.application")
    id("signalmonitor.android.compose")
    id("signalmonitor.scichart.license")
}

val appVersionCode = providers.gradleProperty("VERSION_CODE").map(String::toInt).getOrElse(1)
val appVersionName = providers.gradleProperty("VERSION_NAME").getOrElse("1.0")
val releaseStoreFile = providers.environmentVariable("ANDROID_KEYSTORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull
val isReleaseSigningConfigured =
    listOf(
        releaseStoreFile,
        releaseStorePassword,
        releaseKeyAlias,
        releaseKeyPassword,
    ).all { value -> !value.isNullOrBlank() }
val testCoverageEnabled = providers.gradleProperty("testCoverage").isPresent

android {
    namespace = "com.vadimg778.signalmonitor"

    defaultConfig {
        applicationId = "com.vadimg778.signalmonitor"
        versionCode = appVersionCode
        versionName = appVersionName
    }

    signingConfigs {
        if (isReleaseSigningConfigured) {
            create("release") {
                storeFile = file(requireNotNull(releaseStoreFile))
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = testCoverageEnabled
            enableAndroidTestCoverage = testCoverageEnabled
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.findByName("release")
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:monitor:data"))
    implementation(project(":feature:monitor:presentation"))

    implementation(platform(libs.koin.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.scichart.charting)
    implementation(libs.scichart.core)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(project(":feature:monitor:domain"))
    androidTestImplementation(libs.scichart.data)
    androidTestImplementation(libs.scichart.drawing)
}
