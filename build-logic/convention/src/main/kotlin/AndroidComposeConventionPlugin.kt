import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            extensions.findByType<ApplicationExtension>()?.apply {
                buildFeatures.compose = true
            }
            extensions.findByType<LibraryExtension>()?.apply {
                buildFeatures.compose = true
            }
        }
    }
}
