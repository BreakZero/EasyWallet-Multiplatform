rootProject.name = "EasyWallet"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    maven(uri("https://maven.pkg.github.com/trustwallet/wallet-core")) {
      credentials {
        with(tokenProperty()) {
          username = getProperty("gpr.name") ?: System.getenv("GT_USERNAME")
          password = getProperty("gpr.key") ?: System.getenv("GT_TOKEN")
        }
      }
    }
  }
}


private fun tokenProperty(): java.util.Properties {
  val properties = java.util.Properties()
  val localProperties = File(rootDir, "github_token.properties")

  if (localProperties.isFile) {
    java.io.InputStreamReader(
      java.io.FileInputStream(localProperties)
    ).use { reader ->
      properties.load(reader)
    }
  }
  return properties
}

include(":composeApp")
include(":platform:model")
include(":platform:network")
include(":platform:datastore")
include(":platform:data")
include(":platform:domain")
