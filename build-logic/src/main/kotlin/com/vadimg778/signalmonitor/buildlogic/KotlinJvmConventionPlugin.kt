package com.vadimg778.signalmonitor.buildlogic

import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KotlinJvmConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply("java-library")
        pluginManager.apply("org.jetbrains.kotlin.jvm")
        pluginManager.apply("com.android.lint")
        pluginManager.apply("dev.detekt")
        pluginManager.apply("org.jetbrains.kotlinx.kover")
        extensions.configure<Lint> {
            abortOnError = true
            warningsAsErrors = true
        }
        configureKotlin()
        configureDetekt()
        configureCoverage("jvm")
    }
}
