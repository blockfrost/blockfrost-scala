package io.blockfrost.sdk

import io.blockfrost.sdk.api.{MetricsApi, MetricsApiImpl}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class MetricsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: MetricsApi[Future, Any]
    val env: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: MetricsApi[Future, Any] = new MetricsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: MetricsApi[Future, Any] = new MetricsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getUsageMetrics [${ctx.env}]" should "return array of Metric" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getUsageMetrics
        .extract
        .map(body => {
          body.foreach(m => (m.time > 1 && m.calls >= 0) shouldBe true)
          succeed
        })
    }

    s"getEndpointUsageMetrics [${ctx.env}]" should "return array of EndpointMetric" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getEndpointUsageMetrics
        .extract
        .map(body => {
          body.foreach(m => (m.time >= 0 && m.calls >= 0 && s"${m.endpoint}".nonEmpty) shouldBe true)
          succeed
        })
    }
  }
}
