import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
  id("easy.multiplatform.library")
  kotlin("plugin.serialization") version libs.versions.kotlin
  id("com.codingfeline.buildkonfig") version "0.17.1"
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.ktor.client.okhttp)
        implementation(libs.kotlinx.coroutines.android)
      }
    }
    commonMain {
      dependencies {
        implementation(projects.platform.model)

        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content)
        implementation(libs.ktor.serialization)
        implementation(libs.ktor.logging)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.koin.core)
        implementation(libs.kermit)
      }
    }
    iosMain {
      dependencies {
        implementation(libs.ktor.client.darwin)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.network"
}

buildkonfig {
  packageName = "org.easy.wallet.network"

  defaultConfigs {
    with(readLocalKeys()) {
      buildConfigField(STRING, "ETHERSCAN_KEY", getProperty("etherscan"))
      buildConfigField(STRING, "COINGECKO_KEY", getProperty("COINGECKO_KEY"))
    }
  }
}

private fun readLocalKeys(): Properties {
  val properties = Properties()
  val localProperties = File(rootDir, "configs/apikeys.properties")

  if (localProperties.isFile) {
    InputStreamReader(
      FileInputStream(localProperties)
    ).use { reader ->
      properties.load(reader)
    }
  }
  return properties
}