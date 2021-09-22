package io.blockfrost.sdk

import io.blockfrost.sdk.api.HealthApi.HealthStatus
import io.blockfrost.sdk.api.{HealthApi, HealthApiImpl}
import io.blockfrost.sdk.effect.FutureResponseConverter._
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class HealthApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: HealthApi[Future, Any]
    val env: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: HealthApi[Future, Any] = new HealthApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: HealthApi[Future, Any] = new HealthApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getHealthStatus [${ctx.env}]" should "return HealthStatus" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getHealthStatus
        .extract
        .map(body => {
          body shouldBe HealthStatus(true)
          succeed
        })
    }

    s"getBackendTime [${ctx.env}]" should "return BackendTime" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getBackendTime
        .extract
        .map(body => {
          body.server_time >= 0 shouldBe true
          succeed
        })
    }
  }
}
