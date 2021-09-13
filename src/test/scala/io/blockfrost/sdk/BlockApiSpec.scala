package io.blockfrost.sdk

import io.blockfrost.sdk.api.BlockApi.BlockContent
import io.blockfrost.sdk.api.{BlockApi, BlockApiImpl}
import io.blockfrost.sdk.common.{SortedPageRequest, UnsortedPageRequest}
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class BlockApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getLatestBlock" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getLatestBlock
      .extract
      .map(body => {
        body.next_block shouldBe null
        succeed
      })
  }

  "getLatestBlockTransactions" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getLatestBlockTransactions(SortedPageRequest(1))
      .extract
      .map(body => {
        body.size should (be (1) or be (0))
        succeed
      })
  }

  "getSpecificBlock" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificBlock("1")
      .extract
      .map(body => {
        body should matchPattern { case BlockContent(1564020236, 1, "388a82f053603f3552717d61644a353188f2d5500f4c6354cc1ad27a36a7ea91", 1031, 0, 1031, "ByronGenesis-853b49c9ab5fc52d", 1950, 0, null, null, null, "8f8602837f7c6f8b8867dd1cbc1842cf51a27eaed2c70ef48325d00f8efb320f", "f4e96309537d15682211fcac4c249c2bdff8464476e047be99d80edf97bcf3ff", _) => }
        succeed
      })
  }

  "getSpecificBlockInSlot" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificBlockInSlot("1031")
      .extract
      .map(body => {
        body should matchPattern { case BlockContent(1564020236, 1, "388a82f053603f3552717d61644a353188f2d5500f4c6354cc1ad27a36a7ea91", 1031, 0, 1031, "ByronGenesis-853b49c9ab5fc52d", 1950, 0, null, null, null, "8f8602837f7c6f8b8867dd1cbc1842cf51a27eaed2c70ef48325d00f8efb320f", "f4e96309537d15682211fcac4c249c2bdff8464476e047be99d80edf97bcf3ff", _) => }
        succeed
      })
  }

  "getSpecificBlockInSlotInEpoch" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificBlockInSlotInEpoch("1031", "1")
      .extract
      .map(body => {
        body should matchPattern { case BlockContent(1564452236, 21601, "0336bdc664d787e52f16ca993b13ae448daa00235eb20f457345adc6ee750cdf", 22631, 1, 1031, "ByronGenesis-d6ff29d80d4007f5", 669, 0, null, null, null, "a93ac7224a1aeb8493ed57e695b7527caba840bcfdaf783308e982238df4c411", "d3dba96520e4d813b444c60395f1a4db2a630396d4a503f8ee41f05937243702", _) => }
        succeed
      })
  }

  "getListingOfNextBlocks" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListingOfNextBlocks("1031", UnsortedPageRequest(1))
      .extract
      .map(body => {
        body should matchPattern { case List(BlockContent(1564040856, 1032, "03a7faf4ce8436333fd17b3534744155b720483fd5afe1ba671fc92e95f14416", 2062, 0, 2062, "ByronGenesis-0df4205606dcb8ad", 669, 0, null, null, null, "10fcb8fde6a32bce1e637b173bcf370c1f9d55ea13684f38af2ca622f0d9260e", "6f89b8ef562e5fa3df24225b4ed12040e80df331b59ff6ed3a9a962afc87d575", _)) => }
        succeed
      })
  }

  "getListingOfPreviousBlocks" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListingOfPreviousBlocks("1031", UnsortedPageRequest(1))
      .extract
      .map(body => {
        body should matchPattern { case List(BlockContent(1564040816, 1030, "b81d24a1674f4fb57573b02d5d05122172865d224e9788cd7c30544ded4c49ba", 2060, 0, 2060, "ByronGenesis-44e51b81adce8430", 669, 0, null, null, null, "7500c3525a1da93fe9edb6c2056c18e516fcc257fd3a4a74bbe8dc4d74c01573", "10fcb8fde6a32bce1e637b173bcf370c1f9d55ea13684f38af2ca622f0d9260e", _)) => }
        succeed
      })
  }

  "getBlockTransactions" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getBlockTransactions("1031", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  trait TestContext {
    val api: BlockApi[Future, Any] = new BlockApiImpl[Future, Any] with TestApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
