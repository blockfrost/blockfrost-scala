package io.blockfrost.sdk

import io.blockfrost.sdk.api.AddressesApi._
import io.blockfrost.sdk.api.{AddressesApi, AddressesApiImpl}
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AddressesApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getSpecificAddress" should "return Address" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificAddress("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp")
      .extract
      .map(body => {
        body.address shouldBe "addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp"
        body.stake_address shouldBe "stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl"
        body.`type` shouldBe "shelley"
        body.amount.size shouldBe 2
        succeed
      })
  }

  "getAddressDetails" should "return AddressDetails" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAddressDetails("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp")
      .extract
      .map(body => {
        body.address shouldBe "addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp"
        body.tx_count > 50000 shouldBe true
        body.received_sum.size shouldBe 2
        body.sent_sum.size shouldBe 2
        succeed
      })
  }

  "getAddressUtxos" should "return list of Utxos" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAddressUtxos("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp")
      .extract
      .map(body => {
        body.size shouldBe 2
        val head = body.head
        head.tx_hash.nonEmpty shouldBe true
        head.output_index shouldBe 0
        head.amount.size shouldBe 2
        head.block.nonEmpty shouldBe true

        val tail = body.last
        tail.tx_hash.nonEmpty shouldBe true
        tail.output_index shouldBe 0
        tail.amount.size shouldBe 1
        tail.block.nonEmpty shouldBe true
        succeed
      })
  }

  "getAddressTransactions" should "return list of Transactions" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAddressTransactions("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp", None, None)
      .extract
      .map(body => {
        body.size shouldBe 100
        body should contain(Transaction("40e97bad56d2a8d5f1211142fee1c011edf5e47cdb289bcbeefb3a0e27c51555", 2, 2501652))
        succeed
      })
  }

  "getAddressTransactions with bounds" should "return list of Transactions" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAddressTransactions("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp", Some("8929261"), Some("9999269:10"))
      .extract
      .map(body => {
        body.size shouldBe 0
        succeed
      })
  }

  trait TestContext {
    val api: AddressesApi[Future, Any] = new AddressesApiImpl[Future, Any] with TestApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
