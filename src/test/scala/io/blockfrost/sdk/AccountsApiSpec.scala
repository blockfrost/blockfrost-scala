package io.blockfrost.sdk

import io.blockfrost.sdk.api.AccountsApi.{AccountAddress, Address, Asset}
import io.blockfrost.sdk.api.{AccountsApi, AccountsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.effect.FutureResponseConverter.{ApiError, ApiException, FutureResponseOps}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class AccountsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: AccountsApi[Future, Any]
    val env: String
    val stakeAddress: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: AccountsApi[Future, Any] = new AccountsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val stakeAddress = "stake_test1uqevw2xnsc0pvn9t9r9c7qryfqfeerchgrlm3ea2nefr9hqp8n5xl"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: AccountsApi[Future, Any] = new AccountsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val stakeAddress = "stake1u9j5p3k4sxd4rdfvhddukf2quy9vgf3gnyq98q0t3md3z8cpmu50j"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getSpecificCardanoAddress [${ctx.env}]" should "return AccountAddress" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificCardanoAddress(ctx.stakeAddress)
        .extract
        .map(body => {
          body should matchPattern { case AccountAddress(ctx.stakeAddress, false, 0, _, "0", "0", "0", "0", "0", null) => }
          succeed
        })
    }

    s"getSpecificCardanoAddress with invalid address [${ctx.env}]" should "return error" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificCardanoAddress("1")
        .extract
        .map(_ => fail())
        .recoverWith {
          case ApiException(error) =>
            error shouldBe ApiError(400, "Bad Request", "Invalid or malformed stake address format.")
            succeed
        }
    }

    s"getAccountRewardHistory [${ctx.env}]" should "return sequence of RewardHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountRewardHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountHistory [${ctx.env}]" should "return sequence of RewardHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountDelegationHistory [${ctx.env}]" should "return sequence of DelegationHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountDelegationHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountRegistrationHistory [${ctx.env}]" should "return sequence of RegistrationHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountRegistrationHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountWithdrawalHistory [${ctx.env}]" should "return sequence of WithdrawalHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountWithdrawalHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountMirHistory [${ctx.env}]" should "return sequence of MirHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountMirHistory(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body shouldBe List()
          succeed
        })
    }

    s"getAccountAssociatedAddresses [${ctx.env}]" should "return sequence of Address" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountAssociatedAddresses(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(Address(_)) => }
          succeed
        })
    }

    s"getAccountAssociatedAssets [${ctx.env}]" should "return sequence of Asset" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getAccountAssociatedAssets(ctx.stakeAddress, SortedPageRequest(1))
        .extract
        .map(body => {
          body should matchPattern { case List(Asset(_, _)) => }
          succeed
        })
    }
  }
}
