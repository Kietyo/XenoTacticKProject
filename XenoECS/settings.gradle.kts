pluginManagement {
    val kotlinVersion: String by settings
    val kotlinxBenchmark: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlinx.benchmark") version kotlinxBenchmark
        id("com.soywiz.kproject.settings") version "0.2.7"
    }
}

rootProject.name = "XenoECS"

//include("ktruth")
//project(":ktruth").projectDir = file("..\\ktruth")
