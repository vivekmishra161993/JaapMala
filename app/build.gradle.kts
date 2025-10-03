plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id("com.google.devtools.ksp")
    id ("kotlin-parcelize")
    id("jacoco")
}

android {
    namespace = "com.mtt.jaapmala"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mtt.jaapmala"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.dataStore)
    // Hilt
    implementation(libs.hilt.core)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material3.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // Unit Test
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    // Instrumentation
    androidTestImplementation(libs.androidx.junit)
    // Optional: Lifecycle testing
    testImplementation(libs.lifecycle.runtime.testing)

}
jacoco {
    toolVersion = "0.8.11" // latest stable
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest") // ensure unit tests run first

    group = "verification"
    description = "Generate JaCoCo coverage reports"

    // Report formats
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // Include only domain/data packages
    val includePackages = listOf(
        "com/mtt/jaapmala/data/**/*.*",
        "com/mtt/jaapmala/domain/**/*.*"
    )

    // Exclude UI, DI, generated classes, tests
    val excludePackages = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "com/mtt/jaapmala/main/**/*.*",      // UI
        "com/mtt/jaapmala/di/**/*.*",        // DI modules
        "dagger/hilt/internal/**/*.*"        // Hilt generated
    )

    // Kotlin classes
    val kotlinTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") {
        include(includePackages)
        exclude(excludePackages)
    }

    // Java classes
    val javaTree = fileTree("${buildDir}/intermediates/javac/debug") {
        include(includePackages)
        exclude(excludePackages)
    }

    classDirectories.setFrom(files(javaTree, kotlinTree))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(fileTree(buildDir) {
        include("**/jacoco/testDebugUnitTest.exec")
    })
}

