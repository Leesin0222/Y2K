plugins {
    id("y2k.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.yongjincomapny.y2k.core.ai.impl"
}

dependencies {
    api(project(":core:ai:api"))
    implementation(project(":core:database"))
    implementation(libs.litert)
    implementation(libs.mediapipe.genai)
    implementation(libs.work.runtime)
    implementation(libs.hilt.android)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.work.compiler)
}
