plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:4.1.3")
}

publishing {
    publications {
        create<MavenPublication>("MarsRouterPlugin") {
            from(components["kotlin"])
            groupId = "com.mars.infra"
            artifactId = "mars-router-plugin"
            version = "0.2.23"
        }
//        MarsRouterPlugin(MavenPublication) {
//            from = components.kotlin
//            groupId 'com.mars.infra'
//            artifactId 'mars-router-plugin'
//            version '0.2.23'
//        }
    }
}