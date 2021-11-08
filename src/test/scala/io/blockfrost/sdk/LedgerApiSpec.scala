package io.blockfrost.sdk

import io.blockfrost.sdk.api.LedgerApi.Genesis
import io.blockfrost.sdk.api.{LedgerApi, LedgerApiImpl}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class LedgerApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: LedgerApi[Future, Any]
    val env: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: LedgerApi[Future, Any] = new LedgerApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: LedgerApi[Future, Any] = new LedgerApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getBlockchainGenesis [${ctx.env}]" should "return Genesis" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getBlockchainGenesis
        .extract
        .map(body => {
          body should matchPattern { case Genesis(_, _, "45000000000000000", _, _, _, _, 1, _, _) => }
          succeed
        })
    }
  }
}
