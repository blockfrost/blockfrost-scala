package io.blockfrost.sdk

import io.blockfrost.sdk.api.{ScriptsApi, ScriptsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class ScriptApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: ScriptsApi[Future, Any]
    val env: String
    val scriptHash: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: ScriptsApi[Future, Any] = new ScriptsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val scriptHash: String = "740b419cfe4fda7a90073fd4d4637cc81773e9108207254d7c9d3c75"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: ScriptsApi[Future, Any] = new ScriptsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val scriptHash: String = "f55dfaa24ac08874405206588859ec2804b722a51eb71651433135ef"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getListOfScripts [${ctx.env}]" should "return sequence of ScriptHash" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListOfScripts(SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head.script_hash.nonEmpty shouldBe true
          succeed
        })
    }

    s"getSpecificScript [${ctx.env}]" should "return Script" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificScript(ctx.scriptHash)
        .extract
        .map(body => {
          body.script_hash shouldBe ctx.scriptHash
          succeed
        })
    }

    s"getSpecificScriptRedeemers [${ctx.env}]" should "return sequence of ScriptRedeemer" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificScriptRedeemers(ctx.scriptHash, SortedPageRequest(1))
        .extract
        .map(_ => succeed)
    }
  }
}
