package io.blockfrost.sdk

import io.blockfrost.sdk.api.HealthApi.HealthStatus
import io.blockfrost.sdk.api.HealthApiImpl
import io.blockfrost.sdk.common.Serialization.formats
import io.blockfrost.sdk.common.Testnet
import io.blockfrost.sdk.converter.FutureResponseConverter._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

class HealthApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "healthStatus" should "return HealthStatus" in genericTestContext[TestContext] { ctx =>
    ctx.healthApi
      .getHealthStatus
      .extract
      .map(body => {
        body shouldBe HealthStatus(true)
        succeed
      })
  }

  "backendTime" should "return BackendTime" in genericTestContext[TestContext] { ctx =>
    import io.blockfrost.sdk.converter.FutureResponseConverter._
    ctx.healthApi
      .getBackendTime
      .extract
      .map(body => {
        body.server_time != 0 shouldBe true
        succeed
      })
  }

  trait TestContext {
    lazy val asyncClient: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend()
    val healthApi: HealthApiImpl[Future, Any] = new HealthApiImpl[Future, Any] with ApiClient[Future, Any] {
      override implicit val sttpBackend: SttpBackend[Future, Any] = asyncClient

      override def apiKey: String = sys.env.getOrElse("BLOCKFROST_API_KEY", throw new RuntimeException("Api key not found in environment variables"))

      override def host: String = Testnet.url
    }
  }

  implicit val testContext: TestContext = new TestContext {}
  implicit val serialization: org.json4s.Serialization = org.json4s.jackson.Serialization
}
