plugins {
    id("y2k.android.library")
    id("y2k.android.compose")
}

android {
    namespace = "com.yongjincomapny.y2k.feature.tracklist"
}

dependencies {
    implementation(project(":feature:tracklist:api"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:player:api"))
}
