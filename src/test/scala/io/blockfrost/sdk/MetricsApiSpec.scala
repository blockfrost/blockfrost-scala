package io.blockfrost.sdk

import io.blockfrost.sdk.api.{MetricsApi, MetricsApiImpl}
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class MetricsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getUsageMetrics" should "return array of Metric" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getUsageMetrics
      .extract
      .map(body => {
        body.foreach(m => (m.time > 1 && m.calls >= 0) shouldBe true)
        succeed
      })
  }

  "getEndpointUsageMetrics" should "return array of EndpointMetric" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getEndpointUsageMetrics
      .extract
      .map(body => {
        body.foreach(m => (m.time > 1 && m.calls >= 0 && s"${m.endpoint}".nonEmpty) shouldBe true)
        succeed
      })
  }

  trait TestContext {
    val api: MetricsApi[Future, Any] = new MetricsApiImpl[Future, Any] with TestnetApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
