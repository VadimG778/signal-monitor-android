package com.vadimg778.signalmonitor.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.application")
        pluginManager.apply("dev.detekt")
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        extensions.configure<ApplicationExtension> {
            configureAndroid(this)
            defaultConfig {
                targetSdk = 37
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
            packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            lint {
                checkDependencies = true
                disable += "AndroidGradlePluginVersion"
            }
        }
        configureCoverage("debug")
    }
}
