import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(easy.android.gradlePlugin)
    compileOnly(easy.compose.gradlePlugin)
    compileOnly(easy.kotlin.gradlePlugin)
}

gradlePlugin {
//    plugins.register("androidApplication") {
//        id = "easy.android.application"
//        version = "1.0.0"
//        implementationClass = "org.easy.mobile.convention.plugins.AndroidApplicationConventionPlugin"
//    }

  plugins.register("multiplatformLibrary") {
    id = "easy.multiplatform.library"
    implementationClass = "org.easy.plugins.MultiplatformLibraryPlugin"
  }
  plugins.register("multiplatformFeature") {
    id = "easy.multiplatform.feature"
    implementationClass = "org.easy.plugins.MultiplatformFeaturePlugin"
  }
  plugins.register("multiplatformApplication") {
    id = "easy.multiplatform.application"
    implementationClass = "org.easy.plugins.ComposeApplicationPlugin"
  }
}