plugins {
    id("y2k.android.application")
    id("y2k.android.compose")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.yongjincomapny.y2k"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.yongjincomapny.y2k"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:player:api"))
    implementation(project(":core:player:impl"))

    implementation(project(":feature:home:api"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:search:api"))
    implementation(project(":feature:search:impl"))
    implementation(project(":feature:library:api"))
    implementation(project(":feature:library:impl"))
    implementation(project(":feature:equalizer:api"))
    implementation(project(":feature:equalizer:impl"))
    implementation(project(":feature:profile:api"))
    implementation(project(":feature:profile:impl"))
    implementation(project(":feature:nowplaying:api"))
    implementation(project(":feature:nowplaying:impl"))
    implementation(project(":feature:tracklist:api"))
    implementation(project(":feature:tracklist:impl"))
    implementation(project(":feature:artistdetail:api"))
    implementation(project(":feature:artistdetail:impl"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
