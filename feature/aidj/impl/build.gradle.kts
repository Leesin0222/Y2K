plugins {
    id("y2k.android.library")
    id("y2k.android.compose")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.yongjincomapny.y2k.feature.aidj"
}

dependencies {
    implementation(project(":feature:aidj:api"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:player:api"))
    implementation(project(":core:ai:api"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
