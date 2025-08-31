plugins {
  id("easy.multiplatform.library")
  id("app.cash.sqldelight") version "2.1.0"
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.android.driver)
        implementation(libs.koin.android)
      }
    }
    iosMain {
      dependencies {
        implementation(libs.native.driver)
      }
    }
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        implementation(libs.kotlinx.coroutines.core)

        implementation(libs.koin.core)
      }
    }
  }
}

sqldelight {
  databases {
    create("EasyWalletDatabase") {
      packageName.set("org.easy.wallet.database")
      schemaOutputDirectory.set(file("src/commonMain/sqldelight/schemas"))
    }
  }
}

android {
  namespace = "org.easy.wallet.database"
}