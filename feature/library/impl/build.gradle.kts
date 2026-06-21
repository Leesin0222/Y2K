plugins {
    id("y2k.android.library")
    id("y2k.android.compose")
}

android {
    namespace = "com.yongjincomapny.y2k.feature.library"
}

dependencies {
    implementation(project(":feature:library:api"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:player:api"))
}
