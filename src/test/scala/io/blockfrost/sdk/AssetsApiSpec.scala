package io.blockfrost.sdk

import io.blockfrost.sdk.api.AssetsApi._
import io.blockfrost.sdk.api.{AssetsApi, AssetsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AssetsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getAssets" should "return sequence of AssetShort" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAssets(SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(AssetShort("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", "1"))
        succeed
      })
  }

  "getSpecificAsset" should "return Asset" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificAsset("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e")
      .extract
      .map(body => {
        body shouldBe Asset("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", "476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3", "6e7574636f696e", "asset1jtqefvdycrenq2ly6ct8rwcu5e58va432vj586", "1", "e067ca567df4920f4ac3babc4d805d2afe860e21aa7f6f78dbe8538caf9d8262", 1, None, None)
        succeed
      })
  }

  "getAssetHistory" should "return sequence of AssetHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAssetHistory("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(AssetHistory("e067ca567df4920f4ac3babc4d805d2afe860e21aa7f6f78dbe8538caf9d8262", "1", "minted"))
        succeed
      })
  }

  "getAssetTransactions" should "return sequence of AssetTransaction" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAssetTransactions("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(AssetTransaction("e067ca567df4920f4ac3babc4d805d2afe860e21aa7f6f78dbe8538caf9d8262", 0, 2287021))
        succeed
      })
  }

  "getAssetAddresses" should "return sequence of AssetAddress" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAssetAddresses("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(AssetAddress("addr_test1qqr2l3wvc4ykw6x56ldd7sp5hfu8vesah9wr668ep8trle9re5df3pzwwmyq946axfcejy5n4x0y99wqpgtp2gd0k09qztwpfs", "1"))
        succeed
      })
  }

  "getAssetOfSpecificPolicy" should "return sequence of AssetShort" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAssetOfSpecificPolicy("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c3", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(AssetShort("476039a0949cf0b22f6a800f56780184c44533887ca6e821007840c36e7574636f696e", "1"))
        succeed
      })
  }

  trait TestContext {
    val api: AssetsApi[Future, Any] = new AssetsApiImpl[Future, Any] with TestMainnetApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
