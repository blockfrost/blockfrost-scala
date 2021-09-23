package io.blockfrost.sdk

import io.blockfrost.sdk.api.NetworkApi.{NetworkInfo, Stake, Supply}
import io.blockfrost.sdk.api.{NetworkApi, NetworkApiImpl}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class NetworkApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: NetworkApi[Future, Any]
    val env: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: NetworkApi[Future, Any] = new NetworkApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: NetworkApi[Future, Any] = new NetworkApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getProtocolParameters [${ctx.env}]" should "return EpochProtocolParameters" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getNetworkInformation
        .extract
        .map(body => {
          body should matchPattern { case NetworkInfo(Supply("45000000000000000", _, _), Stake(_, _)) => }
          succeed
        })
    }
  }
}
