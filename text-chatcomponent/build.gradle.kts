plugins {
    kotlin("jvm") version "1.3.70"
}

repositories {
    jcenter()
    maven { setUrl("https://oss.sonatype.org/content/groups/public/") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("net.md-5", "bungeecord-api", "1.15-SNAPSHOT")
    api(project(":text-common"))
}