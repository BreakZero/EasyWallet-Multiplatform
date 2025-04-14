package org.easy.plugins

import com.android.build.api.dsl.ApplicationExtension
import org.easy.configs.configureKotlinAndroid
import org.easy.configs.configureMultiplatformLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class ComposeApplicationPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      apply(plugin = "org.jetbrains.kotlin.multiplatform")
      apply(plugin = "com.android.application")
      apply(plugin = "org.jetbrains.compose")
      apply(plugin = "org.jetbrains.kotlin.plugin.compose")

      val extension = extensions.getByType<ApplicationExtension>()

      configureKotlinAndroid(extension)

      configureMultiplatformLibrary()
    }
  }
}