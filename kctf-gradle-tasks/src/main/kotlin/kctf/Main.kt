package kctf

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

private class MainCommand:  CliktCommand() {
  val testClassesDirs: String by option().required()
  val testRuntimeClasspath: String by option().required()
  val outputDirectoryRelativeToRoot: String by option().required()
  override fun run() {
    kctfGenerateSources(
      testClassesDirs.split(File.pathSeparatorChar).map(::File),
      testRuntimeClasspath.split(File.pathSeparatorChar).map(::File),
      outputDirectoryRelativeToRoot
    )
  }
}

fun main(args: Array<String>) {
  MainCommand().main(args)
}
