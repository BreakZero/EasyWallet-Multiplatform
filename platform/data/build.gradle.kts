plugins {
  id("easy.multiplatform.library")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        implementation(projects.platform.network)
        implementation(projects.platform.datastore)
        implementation(projects.platform.database)


        implementation(libs.sqldelight.coroutines.extensions)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.koin.core)
        api(libs.wallet.core.kotlin)
        api(libs.paging.compose.common)
        implementation(libs.kermit)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.data"
}