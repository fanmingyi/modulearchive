plugins {
    kotlin("jvm") version "1.6.10"
    java
    `java-gradle-plugin`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.16.0"
}


val artifactId="modulearchive"
val groupName = "org.fmy.modulearchive"
val artifactVersion = "1.0-SNAPSHOT"
group = groupName
version = artifactVersion

pluginBundle {
    website = "<substitute your project website>"
    vcsUrl = "<uri to project source repository>"
    tags = listOf("tags", "for", "your", "plugins")
}

repositories {
    google()
    mavenCentral()
}
gradlePlugin {
    plugins {
        create(artifactId) {
            id = project.group as String
            implementationClass = "org.modulearchive.plugin.ModuleArchivePlugin"
        }
    }
}
dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")


    implementation("com.android.tools.build:gradle:7.2.0-alpha06")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}