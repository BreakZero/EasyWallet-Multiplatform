package org.easy.configs

import org.gradle.api.JavaVersion

internal object Version {
  const val compileSdk = 36
  const val minSdk = 33
  const val targetSdk = 36

  val jvmVersion = JavaVersion.VERSION_17
}