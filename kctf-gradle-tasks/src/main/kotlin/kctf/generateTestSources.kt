package kctf

import gratatouille.tasks.GInternal
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5
import java.io.File
import java.net.URLClassLoader

fun kctfGenerateSources(
  testClassesDirs: List<File>,
  testRuntimeClasspath: List<File>,
  @GInternal
  outputDirectoryRelativeToRoot: String,
) {
  File(outputDirectoryRelativeToRoot).apply {
    deleteRecursively()
    mkdirs()
  }

  val urls = (testClassesDirs + testRuntimeClasspath).map { it.toURI().toURL() }.toTypedArray()
  val classloader = URLClassLoader(urls)

  val candidates = testClassesDirs.flatMap { root ->

    root.walk().filter { it.extension == "class" && it.name.startsWith("Abstract") }
      .map {
        val relative = it.relativeTo(root)
        relative.path
          .removeSuffix(".class")
          .replace(File.separatorChar, '.')
      }
  }

  check(candidates.isNotEmpty()) {
    "Kctf: the Kotlin Compiler Test Framework requires at least one file starting with \"Abstract\""
  }

  generateTestGroupSuiteWithJUnit5 {
    candidates.forEach {
      val clazz = classloader.loadClass(it)
      val annotation = clazz.annotations.find { it.annotationClass.simpleName == "TestMetadata" }
      require(annotation != null) {
        "Kctf: abstract test class '${clazz.name} must be annotated with `@TestMetadata`"
      }
      val method = annotation.annotationClass.java.getMethod("value")
      val path = method.invoke(annotation) as String
      require(path.contains(File.separatorChar)) {
        "Kctf: The `@TestMetadata` value must contain a `${File.separatorChar}`."
      }
      val root = path.substringBeforeLast(File.separatorChar)
      val modelPath = path.substringAfterLast(File.separatorChar)
      testGroup(
        testDataRoot = root,
        testsRoot = outputDirectoryRelativeToRoot
      ) {
        testClass(testKClass = clazz, useJunit4 = false) {
          model(modelPath)
        }
      }
    }
  }
  val import = """
    import kctf.ClasspathBasedStandardLibrariesPathProvider;
    import org.jetbrains.kotlin.test.services.KotlinStandardLibrariesPathProvider;
  """.trimIndent()

  val extra = """
    |  @Override
    |  public KotlinStandardLibrariesPathProvider createKotlinStandardLibrariesPathProvider()  {
    |    return ClasspathBasedStandardLibrariesPathProvider.INSTANCE;
    |  }
  """.trimMargin()
  File(outputDirectoryRelativeToRoot).walk().filter {
    it.extension == "java"
  }
    .forEach {
      val new = it.readText()
        .replace("\n}", "\n$extra\n}")
        .replace("import org.jetbrains.kotlin.test.TestMetadata;", "import org.jetbrains.kotlin.test.TestMetadata;\n$import")

      it.writeText(new)
    }
}