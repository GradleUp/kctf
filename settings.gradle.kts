pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
  }

  repositories {
    maven("https://storage.googleapis.com/gradleup/m2")
  }
}

rootProject.name = "kctf-root"

include(":kctf-gradle-plugin", ":kctf-gradle-tasks", ":kctf-runtime")