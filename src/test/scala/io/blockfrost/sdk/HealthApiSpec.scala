package io.blockfrost.sdk

import io.blockfrost.sdk.api.HealthApi.HealthStatus
import io.blockfrost.sdk.api.HealthApiImpl
import io.blockfrost.sdk.converter.FutureResponseConverter._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

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
    val healthApi: HealthApiImpl[Future, Any] = new HealthApiImpl[Future, Any] with TestApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
