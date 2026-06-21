plugins {
    id("y2k.android.library")
    id("y2k.android.compose")
}

android {
    namespace = "com.yongjincomapny.y2k.feature.equalizer"
}

dependencies {
    implementation(project(":feature:equalizer:api"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:player:api"))
}
