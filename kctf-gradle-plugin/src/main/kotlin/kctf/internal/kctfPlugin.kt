package kctf.internal

import gratatouille.wiring.GExtension
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.io.File

@GExtension(pluginId = "com.gradleup.kctf")
abstract class KctfExtension(private val project: Project) {
  init {
    var found = false
    project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
      found = true

      doStuff()
    }

    project.afterEvaluate {
      require(found) {
        "Kctf requires 'org.jetbrains.kotlin.jvm'"
      }
    }
  }

  private fun doStuff() {
    val kotlin = project.extensions.getByType(KotlinJvmProjectExtension::class.java)

    project.registerKctfGenerateSourcesTask(
      testClassesDirs = kotlin.target.compilations.getByName("test").output.classesDirs,
      testRuntimeClasspath = project.configurations.getByName("testRuntimeClasspath"),
      outputDirectoryRelativeToRoot = project.provider {
        project.file("src/test/java").relativeTo(project.rootDir).path
      },
      root = project.provider { project.rootDir.absolutePath },
    )

    project.tasks.named("test").configure {
      it as Test
      it.useJUnitPlatform()
      // Important for IDE support
      it.workingDir = project.rootDir
    }
  }
}

internal fun Project.registerKctfGenerateSourcesTask(
  taskName: String = "kctfGenerateSources",
  testClassesDirs: FileCollection,
  testRuntimeClasspath: FileCollection,
  outputDirectoryRelativeToRoot: Provider<String>,
  root: Provider<String>,
): TaskProvider<*> {
  val configuration = this@registerKctfGenerateSourcesTask.configurations.detachedConfiguration()
  configuration.dependencies.add(dependencies.create("com.gradleup.kctf:kctf-gradle-tasks:$VERSION"))

  /**
   * We need a separate JavaExec task because all the paths require change the working directory.
   *
   * A future version of this should probably use something like a KSP processor instead
   */
  return tasks.register(taskName, JavaExec::class.java) {
    it.classpath(configuration)
    it.setArgs(
      listOf(
        "--test-classes-dirs",
        testClassesDirs.joinToString(File.pathSeparator),
        "--test-runtime-classpath",
        testRuntimeClasspath.joinToString(File.pathSeparator),
        "--output-directory-relative-to-root",
        outputDirectoryRelativeToRoot.get(),
      )
    )
    it.mainClass.set("kctf.MainKt")
    it.workingDir(root.get())
  }
}
