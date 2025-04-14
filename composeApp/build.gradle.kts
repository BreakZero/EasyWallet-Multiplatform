import org.easy.configs.configureFlavors
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
  id("easy.multiplatform.application")
  kotlin("plugin.serialization") version libs.versions.kotlin
  alias(libs.plugins.kotlinCocoapods)
}

kotlin {
  cocoapods {
    version = "1.0.0"
    summary = "Shared module description"
    homepage = "https://example.com"
    ios.deploymentTarget = "16.0"

    framework {
      baseName = "composeApp"
    }
    xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
    xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    dependencies {
      pod("TrustWalletCore", moduleName = "WalletCore")
    }
  }
  androidTarget {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.activity.ktx)
      implementation(libs.koin.android)
      implementation(libs.koin.androidx.compose)

      implementation(libs.vico.compose.m3)
    }
    commonMain.dependencies {
      implementation(projects.platform.data)
      implementation(projects.platform.domain)
      implementation(projects.platform.model)

      implementation(libs.haze)

      implementation(libs.coil.compose)
      implementation(libs.coil.network.ktor3)

      implementation(compose.runtime)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodel)
      implementation(libs.androidx.lifecycle.runtime.compose)
      implementation(libs.navigation.compose)

      implementation(libs.kotlinx.serialization.json)

      implementation(libs.kotlinx.coroutines.core)

      implementation(libs.koin.core)
      implementation(libs.koin.compose)
      implementation(libs.koin.composeVM)

      implementation(libs.lifecycle.viewmodel.compose)
    }
  }
}

android {
  namespace = "org.easy.wallet"

  defaultConfig {
    applicationId = "org.easy.wallet"
    versionCode = 1
    versionName = "1.0"
  }
  configureFlavors(this)
}

dependencies {
  implementation(libs.androidx.foundation.android)
  debugImplementation(compose.uiTooling)
}