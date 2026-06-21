plugins {
    id("y2k.android.library")
}

android {
    namespace = "com.yongjincomapny.y2k.core.player.api"
}

dependencies {
    api(libs.androidx.media3.common)
    api(libs.kotlinx.coroutines.android)
}
