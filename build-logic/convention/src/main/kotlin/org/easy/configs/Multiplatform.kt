package org.easy.configs

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureMultiplatformLibrary() {
  extensions.configure(KotlinMultiplatformExtension::class) {
//    explicitApi()
    androidTarget {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    listOf(
//      iosX64(),
//      iosArm64(),
//      iosSimulatorArm64()
//    ).forEach { iosTarget ->
//      val isDb = project.path.contains(":database") ||
//        project.name.contains("database", ignoreCase = true)
//
//      iosTarget.binaries.framework {
//        baseName = "composeApp"
//        compilerOptions.freeCompilerArgs.addAll("-linker-options", "-lsqlite3")
//      }
//    }
  }
}

internal fun Project.configureMultiplatformAndroid() {
  extensions.configure(KotlinMultiplatformExtension::class) {
    androidTarget {
      compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
      }
    }

    listOf(
      iosX64(),
      iosArm64(),
      iosSimulatorArm64()
    ).forEach { iosTarget ->
      iosTarget.binaries.framework {
        baseName = project.name.replace("-", "_")
        isStatic = true
      }
    }
  }
}
