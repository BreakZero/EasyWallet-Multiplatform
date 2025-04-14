plugins {
  id("easy.multiplatform.library")
  kotlin("plugin.serialization") version libs.versions.kotlin
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.koin.android)
      }
    }
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        implementation(libs.kotlinx.coroutines.core)

        implementation(libs.koin.core)
        implementation(libs.ktor.serialization)
        implementation(libs.androidx.datastore)
        implementation(libs.androidx.datastore.preferences)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.datastore"
}