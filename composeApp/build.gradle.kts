import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.hot.reload)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    composeCompiler {
        // Note: This optimization is not required, but will lead to a better user experience.
        // It is expected that the feature will be enabled by default in future versions of the compiler.
        // https://github.com/JetBrains/compose-hot-reload?tab=readme-ov-file#optimization-enable-optimizenonskippinggroups-not-required
        featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
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
            implementation(libs.material3.adaptive)
            implementation(libs.material3.adaptive.layout)
            implementation(libs.material3.adaptive.navigation)
            implementation(libs.compose.ui.backhandler)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlin.coroutines.swing)
            implementation(libs.ktor.client.java)
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
}

android {
    namespace = "io.github.patxibocos.mycyclist"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.patxibocos.mycyclist"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        // For AGP 4.1+
        isCoreLibraryDesugaringEnabled = true

        // Sets Java compatibility to Java 11
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    coreLibraryDesugaring(libs.desugar.jdk.get())
    detektPlugins(libs.ktlint.detekt.rules.get())
    detektPlugins(libs.twitter.compose.detekt.rules.get())
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