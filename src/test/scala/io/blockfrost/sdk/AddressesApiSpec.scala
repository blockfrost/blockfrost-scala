package io.blockfrost.sdk

import io.blockfrost.sdk.api.AddressesApi._
import io.blockfrost.sdk.api.{AddressesApi, AddressesApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AddressesApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: AddressesApi[Future, Any]
    val env: String
    val address: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: AddressesApi[Future, Any] = new AddressesApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val address = "addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: AddressesApi[Future, Any] = new AddressesApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val address = "addr1q8yrxljz4krnmk83v0sg9vep0u9x5962fhp9y0zzf4dtn9r9grrdtqvm2x6jew6mevj5pcg2csnz3xgq2wq7hrkmzy0satxrft"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getSpecificAddress [${ctx.env}]" should "return Address" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificAddress(ctx.address)
        .extract
        .map(body => {
          body.address shouldBe ctx.address
          body.stake_address.isDefined shouldBe true
          body.`type` shouldBe "shelley"
          body.amount.nonEmpty shouldBe true
          succeed
        })
    }

    s"getAddressDetails [${ctx.env}]" should "return AddressDetails" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAddressDetails(ctx.address)
        .extract
        .map(body => {
          body.address shouldBe ctx.address
          body.tx_count >= 0 shouldBe true
          body.received_sum.nonEmpty shouldBe true
          body.sent_sum.nonEmpty shouldBe true
          succeed
        })
    }

    s"getAddressUtxos [${ctx.env}]" should "return list of Utxos" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAddressUtxos(ctx.address, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          val head = body.head
          head.tx_hash.nonEmpty shouldBe true
          head.output_index shouldBe 0
          head.amount.nonEmpty shouldBe true
          head.block.nonEmpty shouldBe true
          succeed
        })
    }

    s"getAddressTransactions [${ctx.env}]" should "return list of Transactions" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAddressTransactions(ctx.address, None, None, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head should matchPattern { case Transaction(_, _, _) => }
          succeed
        })
    }

    s"getAddressTransactions with bounds [${ctx.env}]" should "return list of Transactions" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAddressTransactions(ctx.address, Some("8929261"), Some("9999269:10"))
        .extract
        .map(body => {
          body.size shouldBe 0
          succeed
        })
    }
  }
}
