package io.blockfrost.sdk

import io.blockfrost.sdk.api.NutlinkApi.{AddressMetadata, Ticker, TickerRecord}
import io.blockfrost.sdk.api.{NutlinkApi, NutlinkApiImpl}
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class NutlinkApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {

  "listMetadata" should "return AddressMetadata" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .listMetadata(ctx.address)
      .extract
      .map(body => {
        body should matchPattern { case AddressMetadata(ctx.address, _, _, _) => }
        succeed
      })
  }

  "listTickers" should "return sequence of Ticker" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .listTickers(ctx.address)
      .extract
      .map(body => {
        body.nonEmpty shouldBe true
        body.foreach(ticker => ticker should matchPattern { case Ticker(_, _, latestBlock) if latestBlock > 0 => })
        succeed
      })
  }

  "listAddressTickerRecords" should "return sequence of TickerRecord" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .listAddressTickerRecords(ctx.address, ctx.ticker)
      .extract
      .map(body => {
        body.nonEmpty shouldBe true
        body.foreach(ticker => ticker should matchPattern { case TickerRecord(_, _, _, _, _) => })
        succeed
      })
  }

  "listTickerRecords" should "return sequence of TickerRecord" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .listTickerRecords(ctx.ticker)
      .extract
      .map(body => {
        body.nonEmpty shouldBe true
        body.foreach(ticker => ticker should matchPattern { case TickerRecord(_, _, _, _, _) => })
        succeed
      })
  }

  trait TestContext {
    val api: NutlinkApi[Future, Any] = new NutlinkApiImpl[Future, Any] with TestnetApiClient
    val address = "addr_test1qpktdfrey07xa2shqe8vjn6rl4mh4xmspccjw3mcvgu67xdk0tx7w22wsvj28pjnx4gygulgex4um9ke3hckwvk8tm9s66tglc"
    val ticker = "ADABTC"
  }

  implicit val testContext: TestContext = new TestContext {}
}
