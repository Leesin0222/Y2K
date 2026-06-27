plugins {
    id("y2k.android.library")
}

android {
    namespace = "com.yongjincomapny.y2k.core.ai.api"
}

dependencies {
    api(project(":core:player:api"))
    api(libs.kotlinx.coroutines.android)
}
