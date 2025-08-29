plugins {
  id("easy.multiplatform.library")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(libs.bignum)
      }
    }
  }
}

android {
  namespace = "org.easy.wallet.model"
}