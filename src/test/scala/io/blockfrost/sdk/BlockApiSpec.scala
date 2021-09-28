package io.blockfrost.sdk

import io.blockfrost.sdk.api.BlockApi.BlockContent
import io.blockfrost.sdk.api.{BlockApi, BlockApiImpl}
import io.blockfrost.sdk.common.{SortedPageRequest, UnsortedPageRequest}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class BlockApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: BlockApi[Future, Any]
    val env: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: BlockApi[Future, Any] = new BlockApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: BlockApi[Future, Any] = new BlockApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getLatestBlock [${ctx.env}]" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getLatestBlock
        .extract
        .map(body => {
          body.next_block shouldBe None
          succeed
        })
    }

    s"getLatestBlockTransactions [${ctx.env}]" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getLatestBlockTransactions(SortedPageRequest(1))
        .extract
        .map(body => {
          body.size should (be(1) or be(0))
          succeed
        })
    }

    s"getSpecificBlock [${ctx.env}]" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificBlock("1")
        .extract
        .map(body => {
          body should matchPattern { case BlockContent(_, Some(_), _, Some(_), Some(_), Some(_), _, _, 0, None, None, None, Some(_), Some(_), _) => }
          succeed
        })
    }

    s"getSpecificBlockInSlot [${ctx.env}]" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificBlockInSlot("1031")
        .extract
        .map(body => {
          body should matchPattern { case BlockContent(_, Some(_), _, Some(_), Some(_), Some(1031), _, _, 0, None, None, None, Some(_), Some(_), _) => }
          succeed
        })
    }

    s"getSpecificBlockInSlotInEpoch [${ctx.env}]" should "return BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificBlockInSlotInEpoch("1031", "1")
        .extract
        .map(body => {
          body should matchPattern { case BlockContent(_, Some(_), _, Some(_), Some(_), Some(1031), _, _, 0, None, None, None, Some(_), Some(_), _) => }
          succeed
        })
    }

    s"getListingOfNextBlocks [${ctx.env}]" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListingOfNextBlocks("1031", UnsortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(BlockContent(_, Some(_), _, Some(_), Some(_), Some(_), _, _, 0, None, None, None, Some(_), Some(_), _)) => }
          succeed
        })
    }

    s"getListingOfPreviousBlocks [${ctx.env}]" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListingOfPreviousBlocks("1031", UnsortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(BlockContent(_, Some(_), _, Some(_), Some(_), Some(_), _, _, 0, None, None, None, Some(_), Some(_), _)) => }
          succeed
        })
    }

    s"getBlockTransactions [${ctx.env}]" should "return sequence of BlockContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getBlockTransactions("1031", SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }
  }
}
