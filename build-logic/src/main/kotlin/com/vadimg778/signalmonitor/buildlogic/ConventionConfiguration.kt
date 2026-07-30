package com.vadimg778.signalmonitor.buildlogic

import com.android.build.api.dsl.CommonExtension
import dev.detekt.gradle.extensions.DetektExtension
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal fun Project.configureAndroid(extension: CommonExtension) {
    extension.apply {
        compileSdk = 37
        defaultConfig.minSdk = 26
        compileOptions.sourceCompatibility = JavaVersion.VERSION_17
        compileOptions.targetCompatibility = JavaVersion.VERSION_17
        lint.abortOnError = true
        lint.warningsAsErrors = true
    }
    configureKotlin()
    configureDetekt()
}

internal fun Project.configureKotlin() {
    extensions.configure<KotlinBaseExtension> {
        jvmToolchain(17)
    }
}

internal fun Project.configureDetekt() {
    val libraries = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies.add(
        "detektPlugins",
        libraries.findLibrary("detekt-rules-ktlint-wrapper").get(),
    )
    extensions.configure<DetektExtension> {
        autoCorrect.set(
            providers.gradleProperty("detektAutoCorrect").map(String::toBoolean).orElse(false),
        )
        buildUponDefaultConfig.set(true)
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        parallel.set(true)
    }
}

internal fun Project.configureCoverage(sourceVariant: String) {
    extensions.configure<KoverProjectExtension> {
        currentProject {
            createVariant("unit") {
                add(sourceVariant)
            }
        }
    }
}
