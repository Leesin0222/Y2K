import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension

internal fun configureAndroidApplication(extension: ApplicationExtension) {
    extension.compileSdk = ProjectConfig.COMPILE_SDK
    extension.defaultConfig {
        minSdk = ProjectConfig.MIN_SDK
        targetSdk = ProjectConfig.TARGET_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    extension.compileOptions {
        sourceCompatibility = ProjectConfig.JAVA_VERSION
        targetCompatibility = ProjectConfig.JAVA_VERSION
    }
}

internal fun configureAndroidLibrary(extension: LibraryExtension) {
    extension.compileSdk = ProjectConfig.COMPILE_SDK
    extension.defaultConfig {
        minSdk = ProjectConfig.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    extension.compileOptions {
        sourceCompatibility = ProjectConfig.JAVA_VERSION
        targetCompatibility = ProjectConfig.JAVA_VERSION
    }
}
