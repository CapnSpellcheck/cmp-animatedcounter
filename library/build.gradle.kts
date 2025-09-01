import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.androidLibrary)
//    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.compose.compiler)
    id("maven-publish")
}

group = "com.letstwinkle"
version = "1.0.0"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(compose.foundation)
                implementation(compose.ui)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "com.letstwinkle"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/CapnSpellcheck/cmp-animatedcounter")
            credentials {
                username = System.getenv("GITHUB_USER") ?: project.properties["GITHUB_USER"] as String
                password = System.getenv("GITHUB_PERSONAL_ACCESS_TOKEN") ?: project.properties["GITHUB_PERSONAL_ACCESS_TOKEN"] as String
            }
        }
    }
    publications.withType<MavenPublication> {
        pom {
            name = "cmp-animatedcounter"
            description = "A counter widget that animates new values by sliding digits into place, using Compose Multiplatform."
            inceptionYear = "2025"
            url = "https://github.com/CapnSpellcheck/cmp-animatedcounter"
            licenses {
                license {
                    name = "GNU GPL v3"
                    url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                    distribution = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                }
                developers {
                    developer {
                        name = "Captain Spellcheck"
                        organization = "Github"
                        organizationUrl = "https://github.com/"
                    }
                }
                scm {
                    url = "https://github.com/CapnSpellcheck/cmp-animatedcounter"
                    connection = "scm:git:git://github.com/CapnSpellcheck/cmp-animatedcounter.git"
                    developerConnection = "scm:git:ssh://git@github.com/CapnSpellcheck/cmp-animatedcounter.git"

                }
            }
        }
    }
}