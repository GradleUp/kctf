import com.gradleup.librarian.gradle.Librarian

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.google.devtools.ksp")
  id("com.gradleup.gratatouille.tasks")
}

Librarian.module(project)

gratatouille {
  codeGeneration {
    classLoaderIsolation()
  }
}

dependencies {
  implementation(libs.kotlin.compiler.internal.test.framework)
  implementation(libs.kotlin.compiler)
  implementation(libs.clikt)
  implementation(libs.kotlin.test.junit5)
}

