package io.blockfrost.sdk

import io.blockfrost.sdk.api.EpochsApi.{Epoch, EpochProtocolParameters}
import io.blockfrost.sdk.api.{EpochsApi, EpochsApiImpl}
import io.blockfrost.sdk.common.{SortedPageRequest, UnsortedPageRequest}
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class EpochApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getLatestEpoch" should "return Epoch" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getLatestEpoch
      .extract
      .map(body => {
        body.epoch > 0 shouldBe true
        body.start_time <= System.currentTimeMillis() shouldBe true
        body.end_time <= System.currentTimeMillis() shouldBe true
        body.first_block_time < System.currentTimeMillis() shouldBe true
        body.last_block_time < System.currentTimeMillis() shouldBe true
        body.block_count >= 0 shouldBe true
        body.tx_count >= 0 shouldBe true
        body.output.nonEmpty shouldBe true
        body.fees.nonEmpty shouldBe true
        body.active_stake.nonEmpty shouldBe true
        succeed
      })
  }

  "getLatestEpochProtocolParameters" should "return EpochProtocolParameters" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getLatestEpochProtocolParameters
      .extract
      .map(body => {
        body.epoch > 0 shouldBe true
        body.min_fee_a >= 0 shouldBe true
        body.min_fee_b >= 0 shouldBe true
        body.max_block_size >= 0 shouldBe true
        body.max_tx_size >= 0 shouldBe true
        body.max_block_header_size >= 0 shouldBe true
        body.key_deposit.nonEmpty shouldBe true
        body.min_utxo.nonEmpty shouldBe true
        body.min_pool_cost.nonEmpty shouldBe true
        body.nonce.nonEmpty shouldBe true
        succeed
      })
  }

  "getSpecificEpoch" should "return Epoch" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificEpoch(1)
      .extract
      .map(body => {
        body shouldBe ctx.firstEpoch
        succeed
      })
  }

  "getListingOfNextEpochs" should "return sequence of Epochs" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListingOfNextEpochs(0, UnsortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head shouldBe ctx.firstEpoch
        succeed
      })
  }

  "getListingOfPreviousEpochs" should "return sequence of Epochs" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListingOfPreviousEpochs(2, UnsortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head shouldBe ctx.firstEpoch
        succeed
      })
  }

  "getStakeDistribution" should "return sequence of StakeDistributions" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakeDistribution(1, UnsortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 0
        succeed
      })
  }

  "getStakeDistributionByPool" should "return sequence of StakeDistributions" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakeDistributionByPool(1, "", UnsortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 0
        succeed
      })
  }

  "getBlockDistribution" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getBlockDistribution(1, SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head shouldBe "7e8b2df7730261d8831fe0206591570734d353c15d5266b7fe77097090d33cbd"
        succeed
      })
  }

  "getBlockDistributionByPool" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getBlockDistributionByPool(1, "", SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head shouldBe "7e8b2df7730261d8831fe0206591570734d353c15d5266b7fe77097090d33cbd"
        succeed
      })
  }

  "getProtocolParameters" should "return EpochProtocolParameters" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getProtocolParameters(100)
      .extract
      .map(body => {
        body shouldBe EpochProtocolParameters(100, 44, 155381, 65536, 16384, 1100, "2000000", "500000000", 18, 500, 0.3, 0.003, 0.2, 0.25, None, 2, 0, "1000000", "340000000", "03b4c293000b19771f7c96dbdaead1b7071944f4a28b83cb9fddba19e2947211", None, None, None, None, None, None, None, None, None, None)
        succeed
      })
  }

  trait TestContext {
    val api: EpochsApi[Future, Any] = new EpochsApiImpl[Future, Any] with TestnetApiClient
    val firstEpoch: Epoch = Epoch(1, 1564431616, 1564863616, 1564431616, 1564863596, 21601, 305, "152336265877919", "54105620", None)
  }

  implicit val testContext: TestContext = new TestContext {}
}
