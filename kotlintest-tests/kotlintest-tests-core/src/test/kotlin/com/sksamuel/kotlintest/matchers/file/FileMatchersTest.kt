package com.sksamuel.kotlintest.matchers.file

import io.kotlintest.matchers.file.aDirectory
import io.kotlintest.matchers.file.aFile
import io.kotlintest.matchers.file.beAbsolute
import io.kotlintest.matchers.file.beRelative
import io.kotlintest.matchers.file.exist
import io.kotlintest.matchers.file.haveExtension
import io.kotlintest.matchers.file.shouldBeADirectory
import io.kotlintest.matchers.file.shouldBeAFile
import io.kotlintest.matchers.file.shouldBeAbsolute
import io.kotlintest.matchers.file.shouldBeRelative
import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.matchers.file.shouldHaveExtension
import io.kotlintest.matchers.file.shouldNotBeADirectory
import io.kotlintest.matchers.file.shouldNotBeAFile
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.matchers.file.shouldNotHaveExtension
import io.kotlintest.matchers.file.shouldStartWithPath
import io.kotlintest.matchers.file.startWithPath
import io.kotlintest.matchers.string.shouldEndWith
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileMatchersTest : FunSpec() {
  private val osName = System.getProperty("os.name").toLowerCase()
  private val isWindows = osName.contains("windows")

  init {

    test("relative() should match only relative files") {
      File("sammy/boy") shouldBe beRelative()
      File("sammy/boy").shouldBeRelative()
    }

    test("absolute() should match only absolute files") {
      val root = if (isWindows) "C:/" else "/"
      File("${root}sammy/boy") shouldBe beAbsolute()
      File("${root}sammy/boy").shouldBeAbsolute()
    }

    test("startWithPath() should only match files that start with the given path") {
      File("sammy/boy") should startWithPath("sammy")
      File("sammy/boy") should startWithPath(Paths.get("sammy"))
      File("/sammy/boy") should startWithPath("${File.separator}sammy")
      File("/sammy/boy") should startWithPath(Paths.get("/sammy"))

      File("/sammy/boy").shouldStartWithPath("${File.separator}sammy")
      File("/sammy/boy").shouldStartWithPath(Paths.get("/sammy"))
    }

    test("exist() file matcher") {
      val file = Files.createTempFile("test", "test").toFile()
      file should exist()

      shouldThrow<AssertionError> {
        File("qweqwewqewqewee") should exist()
      }.message shouldBe "File qweqwewqewqewee should exist"

      file.shouldExist()

      shouldThrow<AssertionError> {
        file.shouldNotExist()
      }

      file.delete()
    }

    test("haveExtension") {
      val file = Files.createTempFile("test", ".test").toFile()
      file should haveExtension(".test")
      file shouldNot haveExtension(".wibble")

      shouldThrow<AssertionError> {
        file should haveExtension(".jpeg")
      }.message!!.shouldEndWith("with one of .jpeg")

      shouldThrow<AssertionError> {
        file.shouldHaveExtension(".jpeg")
      }.message!!.shouldEndWith("with one of .jpeg")

      file.shouldHaveExtension(".test")
      file.shouldNotHaveExtension(".wibble")

      file.delete()
    }

    test("aFile() file matcher") {
      val file = Files.createTempFile("test", "test").toFile()
      file shouldBe aFile()
      file.shouldBeAFile()

      shouldThrow<AssertionError> {
        file shouldBe aDirectory()
      }

      shouldThrow<AssertionError> {
        file.shouldNotBeAFile()
      }

      shouldThrow<AssertionError> {
        file.shouldBeADirectory()
      }

      file.delete()
    }

    test("aDirectory() file matcher") {
      val dir = Files.createTempDirectory("testdir").toFile()
      dir shouldBe aDirectory()
      dir.shouldBeADirectory()

      shouldThrow<AssertionError> {
        dir shouldBe aFile()
      }

      shouldThrow<AssertionError> {
        dir.shouldNotBeADirectory()
      }

      shouldThrow<AssertionError> {
        dir shouldBe aFile()
      }
    }
  }
}