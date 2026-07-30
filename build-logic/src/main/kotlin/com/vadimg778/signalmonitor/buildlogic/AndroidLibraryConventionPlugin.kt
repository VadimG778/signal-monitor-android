package com.vadimg778.signalmonitor.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("dev.detekt")
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        extensions.configure<LibraryExtension> {
            configureAndroid(this)
        }
        configureCoverage("debug")
    }
}
