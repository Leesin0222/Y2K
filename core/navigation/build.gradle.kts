plugins {
    id("y2k.android.library")
    id("y2k.android.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yongjincomapny.y2k.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)
    api(libs.kotlinx.serialization.json)
}
