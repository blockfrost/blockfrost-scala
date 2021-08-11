package io.blockfrost.sdk

import io.blockfrost.sdk.api.MetricsApiImpl
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class MetricsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {

  "getUsageMetrics" should "return array of Metric" in genericTestContext[TestContext] { ctx =>
    ctx.metricsApi
      .getUsageMetrics
      .extract
      .map(body => {
        body.foreach(m => (m.time > 1 && m.calls >= 0) shouldBe true)
        succeed
      })
  }

  "getEndpointUsageMetrics" should "return array of EndpointMetric" in genericTestContext[TestContext] { ctx =>
    ctx.metricsApi
      .getEndpointUsageMetrics
      .extract
      .map(body => {
        body.foreach(m => (m.time > 1 && m.calls >= 0 && s"${m.endpoint}".nonEmpty) shouldBe true)
        succeed
      })
  }

  trait TestContext {
    val metricsApi: MetricsApiImpl[Future, Any] = new MetricsApiImpl[Future, Any] with TestApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
