plugins {
    `java-library`
    java
}

repositories {
    jcenter()
}

dependencies {
    api(project(":text-common"))
}

allprojects {
    buildDir = File("$rootDir/build")
}
