package io.blockfrost.sdk

import io.blockfrost.sdk.api.AccountsApi.{AccountAddress, Address, Asset}
import io.blockfrost.sdk.api.{AccountsApi, AccountsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.converter.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AccountsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getSpecificCardanoAddress" should "return AccountAddress" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificCardanoAddress("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl")
      .extract
      .map(body => {
        body should matchPattern { case AccountAddress("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", false, 0, _, "0", "0", "0", "0", "0", null) => }
        succeed
      })
  }

  "getAccountRewardHistory" should "return sequence of RewardHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountRewardHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  "getAccountHistory" should "return sequence of RewardHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  "getAccountDelegationHistory" should "return sequence of DelegationHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountDelegationHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  "getAccountRegistrationHistory" should "return sequence of RegistrationHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountRegistrationHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  "getAccountWithdrawalHistory" should "return sequence of WithdrawalHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountWithdrawalHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }
  "getAccountMirHistory" should "return sequence of MirHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountMirHistory("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List()
        succeed
      })
  }

  "getAccountAssociatedAddresses" should "return sequence of Address" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountAssociatedAddresses("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(Address("addr_test1qz2fxv2umyhttkxyxp8x0dlpdt3k6cwng5pxj3jhsydzer3jcu5d8ps7zex2k2xt3uqxgjqnnj83ws8lhrn648jjxtwq2ytjqp"))
        succeed
      })
  }

  "getAccountAssociatedAssets" should "return sequence of Asset" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getAccountAssociatedAssets("stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl", SortedPageRequest(1))
      .extract
      .map(body => {
        body shouldBe List(Asset("b01fb3b8c3dd6b3705a5dc8bcd5a70759f70ad5d97a72005caeac3c652657675746f31333237", "1"))
        succeed
      })
  }

  trait TestContext {
    val api: AccountsApi[Future, Any] = new AccountsApiImpl[Future, Any] with TestMainnetApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
