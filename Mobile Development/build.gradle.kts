buildscript {
    dependencies {
        classpath (libs.androidx.navigation.safe.args.gradle.plugin)
    }
}

plugins {
    id ("com.android.application") version ("8.3.0") apply false
    id ("org.jetbrains.kotlin.android") version ("1.9.0") apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
