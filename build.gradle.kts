plugins {
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":app"))
    kover(project(":core:designsystem"))
    kover(project(":feature:monitor:data"))
    kover(project(":feature:monitor:domain"))
    kover(project(":feature:monitor:presentation"))
}

kover {
    currentProject {
        createVariant("unit") {
        }
    }
    reports {
        filters {
            excludes {
                androidGeneratedClasses()
            }
        }
    }
}
