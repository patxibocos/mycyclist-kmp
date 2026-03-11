import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.hot.reload)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "io.github.patxibocos.mycyclist.composeapp"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compose.components.ui.tooling.preview)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3.adaptive.navigation.suite)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.ktor.client.core)
            implementation(libs.firebase.common)
            implementation(libs.firebase.config)
            implementation(libs.firebase.messaging)
            implementation(libs.kotlin.serialization.protobuf)
            implementation(libs.kotlin.collections.immutable)
            implementation(libs.kotlin.datetime)
            implementation(libs.material3.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.navigation3.material3.adaptive)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.google.oauth2.http)
            implementation(libs.kotlin.coroutines.swing)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.java)
            implementation(libs.ktor.serialization.json)
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Some description for a Kotlin/Native module"
        homepage = "Link to a Kotlin/Native module homepage"

        framework {
            baseName = "composeApp"

            // Optional properties
            // Specify the framework linking type. It's dynamic by default.
            isStatic = false
        }

        ios.deploymentTarget = "18.0"

        pod("FirebaseCore", linkOnly = true)
        pod("FirebaseRemoteConfig", linkOnly = true)
        pod("FirebaseMessaging", linkOnly = true)

        podfile = project.file("../iosApp/Podfile")
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.time.ExperimentalTime",
            "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=kotlin.io.encoding.ExperimentalEncodingApi",
        )
    }
}

dependencies {
    "androidCompileClasspath"(platform(libs.firebase.bom))
    "androidRuntimeClasspath"(platform(libs.firebase.bom))
    detektPlugins(libs.ktlint.detekt.rules)
    detektPlugins(libs.twitter.compose.detekt.rules)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

tasks.withType<Detekt> {
    val buildDir = project.layout.buildDirectory.asFile.get()
    exclude {
        it.file.relativeTo(projectDir).startsWith(buildDir.relativeTo(projectDir))
    }
}
