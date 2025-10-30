import org.easy.configs.configureFlavors
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

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
      isStatic = true
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

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "composeApp"
      isStatic = true
      linkerOpts("-lsqlite3")
    }
  }

  compilerOptions {
    optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
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
      implementation(projects.platform.datastore)

      implementation("org.jetbrains.compose.material3:material3:1.9.0-alpha04")

      implementation(libs.haze)

      implementation(libs.coil.compose)
      implementation(libs.coil.network.ktor3)

      implementation(compose.runtime)
//      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
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

      implementation(libs.qr.kit)
    }
  }
}

android {
  namespace = "org.easy.wallet"

  defaultConfig {
    applicationId = "org.easy.wallet"
    versionCode = 1
    versionName = "1.0"
    ndk {
      //noinspection ChromeOsAbiSupport
      abiFilters += listOf("armeabi-v7a", "arm64-v8a")
    }
  }

  signingConfigs {
    create("release") {
      with(keyStoreProperties()) {
        storeFile = rootProject.file(getProperty("storeFile"))
        storePassword = getProperty("storePassword")
        keyAlias = getProperty("keyAlias")
        keyPassword = getProperty("keyPassword")
      }
    }
  }
  buildTypes {
    debug {
      isMinifyEnabled = false
      applicationIdSuffix = ".debug"
    }
    release {
      isMinifyEnabled = providers.gradleProperty("minifyWithR8")
        .map(String::toBooleanStrict).getOrElse(true)
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }

  configureFlavors(this)
}

private fun keyStoreProperties(): Properties {
  val properties = Properties()
  val keyProperties = rootProject.file("keystore/keystore.properties")

  if (keyProperties.isFile) {
    InputStreamReader(FileInputStream(keyProperties), Charsets.UTF_8).use { reader ->
      properties.load(reader)
    }
  }
  return properties
}

dependencies {
  implementation(libs.androidx.foundation.android)
  debugImplementation(compose.uiTooling)
}