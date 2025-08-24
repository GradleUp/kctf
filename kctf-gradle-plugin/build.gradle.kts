import com.gradleup.librarian.gradle.Librarian

plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.google.devtools.ksp")
  id("com.gradleup.gratatouille")
}

Librarian.module(project)

gratatouille {
  codeGeneration()
}

dependencies {
  compileOnly(libs.gradle.api)
  compileOnly(libs.kgp.compile.only)
  compileOnly(project(":kctf-gradle-tasks"))
//  gratatouille(project(":kctf-gradle-tasks"))
}