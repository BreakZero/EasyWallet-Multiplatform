plugins {
  id("easy.multiplatform.library")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        api(projects.platform.data)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.koin.core)
        implementation(libs.kermit)
        api(libs.paging.compose.common)
        api(libs.wallet.core.kotlin)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.domain"
}