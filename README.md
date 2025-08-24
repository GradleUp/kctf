# Kctf

Kctf is a Gradle Plugin for the Kotlin Compiler Test Framework (Kctf).

It factors some of the Gradle configuration and boilerplate needed to setup tests for your Kotlin compiler plugin.

## Get started

Configure your build

```kotlin
plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.gradleup.kctf").version("2.2.20-RC-0.0.0")
}

dependencies {
  // Add the kctf runtime, it pulls the kotlin test framework transitively
  testImplementation(libs.kctf.runtime)
}
```

Write your abstract tests:

```kotlin
@TestMetadata("compiler-tests/src/test/data/box")
open class AbstractBoxTest : AbstractFirLightTreeBlackBoxCodegenTest() {
  override fun createKotlinStandardLibrariesPathProvider(): KotlinStandardLibrariesPathProvider {
    return ClasspathBasedStandardLibrariesPathProvider
  }

  override fun configure(builder: TestConfigurationBuilder) {
    super.configure(builder)

    with(builder) {
      configurePlugin()

      defaultDirectives {
        +FULL_JDK
        +WITH_STDLIB
        +IGNORE_DEXING
      }
    }
  }
}
```

Kctf generates your test in `src/test/java`.