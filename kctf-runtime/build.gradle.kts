import com.gradleup.librarian.gradle.Librarian

plugins {
  id("org.jetbrains.kotlin.jvm")
}

Librarian.module(project)

dependencies {
  api(libs.kotlin.compiler.internal.test.framework)
  api(libs.kotlin.compiler)
  api(libs.kotlin.test.junit5)
  
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlin.script.runtime)
  implementation(libs.kotlin.annotations.jvm)
}

