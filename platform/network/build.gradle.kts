plugins {
  id("easy.multiplatform.library")
  kotlin("plugin.serialization") version libs.versions.kotlin
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