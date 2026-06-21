plugins {
    id("y2k.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yongjincomapny.y2k.feature.tracklist.api"
}

dependencies {
    implementation(project(":core:navigation"))
}
