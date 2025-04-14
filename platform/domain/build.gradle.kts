plugins {
  id("easy.multiplatform.library")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        implementation(projects.platform.data)
        implementation(projects.platform.datastore)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.koin.core)
        implementation(libs.kermit)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.domain"
}