import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kgp)
  alias(libs.plugins.librarian)
  alias(libs.plugins.gratatouille)
  alias(libs.plugins.ksp)
}

Librarian.root(project)