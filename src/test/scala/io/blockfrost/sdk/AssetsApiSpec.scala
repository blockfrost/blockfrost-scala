package io.blockfrost.sdk

import io.blockfrost.sdk.api.AssetsApi._
import io.blockfrost.sdk.api.{AssetsApi, AssetsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AssetsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: AssetsApi[Future, Any]
    val env: String
    val asset: String
    val assetHistoryTxHash: String
    val policyId: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: AssetsApi[Future, Any] = new AssetsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val asset = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e"
    val assetHistoryTxHash: String = "e067ca567df4920f4ac3babc4d805d2afe860e21aa7f6f78dbe8538caf9d8262"
    val policyId: String = "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: AssetsApi[Future, Any] = new AssetsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val asset = "00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae6e7574636f696e"
    val assetHistoryTxHash: String = "e252be4c7e40d35919f741c9649ff207c3e49d53bb819e5c1cb458055fd363ed"
    val policyId: String = "00000002df633853f6a47465c9496721d2d5b1291b8398016c0e87ae"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getAssets [${ctx.env}]" should "return sequence of AssetShort" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAssets(SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List(AssetShort(ctx.asset, "1"))
          succeed
        })
    }

    s"getSpecificAsset [${ctx.env}]" should "return Asset" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificAsset(ctx.asset)
        .extract
        .map(body => {
          body should matchPattern { case Asset(ctx.asset, _, Some(_), _, "1", _, 1, _, _) => }
          succeed
        })
    }

    s"getAssetHistory [${ctx.env}]" should "return sequence of AssetHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAssetHistory(ctx.asset, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List(AssetHistory(ctx.assetHistoryTxHash, "1", "minted"))
          succeed
        })
    }

    s"getAssetTransactions [${ctx.env}]" should "return sequence of AssetTransaction" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAssetTransactions(ctx.asset, SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(AssetTransaction(ctx.assetHistoryTxHash, _, _)) => }
          succeed
        })
    }

    s"getAssetAddresses [${ctx.env}]" should "return sequence of AssetAddress" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAssetAddresses(ctx.asset, SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(AssetAddress(_, "1")) => }
          succeed
        })
    }

    s"getAssetOfSpecificPolicy [${ctx.env}]" should "return sequence of AssetShort" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAssetOfSpecificPolicy(ctx.policyId, SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(AssetShort(_, "1")) => }
          succeed
        })
    }
  }
}
