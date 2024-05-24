buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath(libs.google.services)
    }
    //ext.anko_version='0.10.5'
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}