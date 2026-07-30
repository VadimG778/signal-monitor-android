package com.vadimg778.signalmonitor.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class UnitTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val libraries = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies.add("testImplementation", libraries.findLibrary("junit").get())
            dependencies.add(
                "testImplementation",
                libraries.findLibrary("kotlinx-coroutines-test").get(),
            )
        }
    }
}
