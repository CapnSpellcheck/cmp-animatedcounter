pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "cmp-animatedcounter"
include(":library")
for (child in rootProject.children) {
    if (child.name == "library")
        child.name = "cmp-animatedcounter"
}