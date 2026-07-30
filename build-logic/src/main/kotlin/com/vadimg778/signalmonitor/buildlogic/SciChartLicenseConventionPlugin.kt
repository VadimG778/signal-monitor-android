package com.vadimg778.signalmonitor.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class SciChartLicenseConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val localLicenseFile = rootProject.layout.projectDirectory.file("key.txt")
        val localLicenseKey = if (localLicenseFile.asFile.isFile) {
                providers.fileContents(localLicenseFile).asText.map(String::trim)
            } else {
                providers.provider { "" }
            }
        val licenseKey = providers.gradleProperty("SCICHART_LICENSE_KEY")
                .orElse(providers.environmentVariable("SCICHART_LICENSE_KEY"))
                .orElse(localLicenseKey)
                .map(String::trim)
                .getOrElse("")

        pluginManager.withPlugin("com.android.application") {
            extensions.getByType<ApplicationExtension>().apply {
                buildFeatures.resValues = true
                defaultConfig.resValue("string", "scichart_license_key", licenseKey)
            }
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.getByType<LibraryExtension>().apply {
                buildFeatures.resValues = true
                defaultConfig.resValue("string", "scichart_license_key", licenseKey)
            }
        }
    }
}
