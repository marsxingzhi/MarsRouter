// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
//    ext.kotlin_version = "1.4.32"
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("com.mars.infra:mars-router-plugin:0.3.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("$rootDir/maven")
        }
    }
}

//task clean(type: Delete) {
//    delete rootProject.buildDir
//}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}