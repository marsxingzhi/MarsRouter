import org.gradle.internal.jvm.Jvm
plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation(project(":router-api"))
    implementation("com.squareup:javapoet:1.9.0")
    // Project Structure中修改JDK Location，使用本地的，否则报空指针
    compileOnly(files(Jvm.current().toolsJar))
}