package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
abstract class AbstractShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  operator fun String.invoke(init: ShouldScope.() -> Unit) =
      addTestCase(this, { this@AbstractShouldSpec.ShouldScope(this).init() }, defaultTestCaseConfig)

  fun should(name: String, test: TestContext.() -> Unit) =
      addTestCase("should $name", test, defaultTestCaseConfig)

  fun should(name: String) = Testbuilder({ test, config -> addTestCase("should $name", test, config) })

  inner class Testbuilder(val register: (TestContext.() -> Unit, TestCaseConfig) -> Unit) {
    fun config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      register(test, config)
    }
  }

  @KotlinTestDsl
  inner class ShouldScope(val context: TestContext) {

    operator fun String.invoke(init: ShouldScope.() -> Unit) =
        context.registerTestCase(this, this@AbstractShouldSpec, { this@AbstractShouldSpec.ShouldScope(this).init() }, this@AbstractShouldSpec.defaultTestCaseConfig)

    fun should(name: String, test: TestContext.() -> Unit) =
        context.registerTestCase("should $name", this@AbstractShouldSpec, test, this@AbstractShouldSpec.defaultTestCaseConfig)

    fun should(name: String) =
        this@AbstractShouldSpec.Testbuilder({ test, config -> context.registerTestCase("should $name", this@AbstractShouldSpec, test, config) })

  }
}