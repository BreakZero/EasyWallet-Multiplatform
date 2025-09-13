plugins {
  id("easy.multiplatform.library")
  id("app.cash.sqldelight") version "2.1.0"
  kotlin("plugin.serialization") version libs.versions.kotlin
}

kotlin {
  sourceSets {
    androidMain {
      dependencies {
        implementation(libs.sqldelight.android.driver)
        implementation(libs.koin.android)
      }
    }
    iosMain {
      dependencies {
        implementation(libs.sqldelight.native.driver)
      }
    }
    commonMain {
      dependencies {
        implementation(projects.platform.model)
        implementation(libs.kotlinx.coroutines.core)

        implementation(libs.kotlinx.serialization.json)

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
      version = 2
    }
  }
  linkSqlite.set(true)
}

android {
  namespace = "org.easy.wallet.database"
}