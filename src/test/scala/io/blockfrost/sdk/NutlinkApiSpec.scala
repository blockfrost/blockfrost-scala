package io.blockfrost.sdk

import io.blockfrost.sdk.api.NutlinkApi.{AddressMetadata, Ticker, TickerRecord}
import io.blockfrost.sdk.api.{NutlinkApi, NutlinkApiImpl}
import io.blockfrost.sdk.util.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class NutlinkApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {

  trait TestContext {
    val api: NutlinkApi[Future, Any]
    val env: String
    val address: String
    val ticker: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: NutlinkApi[Future, Any] = new NutlinkApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val address = "addr_test1qpktdfrey07xa2shqe8vjn6rl4mh4xmspccjw3mcvgu67xdk0tx7w22wsvj28pjnx4gygulgex4um9ke3hckwvk8tm9s66tglc"
    val ticker = "ADABTC"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: NutlinkApi[Future, Any] = new NutlinkApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val address = "addr1q85yx2w7ragn5sx6umgmtjpc3865s9sg59sz4rrh6f90kgwfwlzu3w8ttacqg89mkdgwshwnplj5c5n9f8dhp0h55q2q7qm63t"
    val ticker = "ADABTC"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"listMetadata [${ctx.env}]" should "return AddressMetadata" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .listMetadata(ctx.address)
        .extract
        .map(body => {
          body should matchPattern { case AddressMetadata(ctx.address, _, _, _) => }
          succeed
        })
    }

    s"listTickers [${ctx.env}]" should "return sequence of Ticker" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .listTickers(ctx.address)
        .extract
        .map(body => {
          body.nonEmpty shouldBe true
          body.foreach(ticker => ticker should matchPattern { case Ticker(_, _, latestBlock) if latestBlock > 0 => })
          succeed
        })
    }

    s"listAddressTickerRecords [${ctx.env}]" should "return sequence of TickerRecord" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .listAddressTickerRecords(ctx.address, ctx.ticker)
        .extract
        .map(body => {
          body.nonEmpty shouldBe true
          body.foreach(ticker => ticker should matchPattern { case TickerRecord(_, _, _, _, _) => })
          succeed
        })
    }

    s"listTickerRecords [${ctx.env}]" should "return sequence of TickerRecord" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .listTickerRecords(ctx.ticker)
        .extract
        .map(body => {
          body.nonEmpty shouldBe true
          body.foreach(ticker => ticker should matchPattern { case TickerRecord(_, _, _, _, _) => })
          succeed
        })
    }
  }
}
