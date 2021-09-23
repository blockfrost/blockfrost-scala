package io.blockfrost.sdk

import io.blockfrost.sdk.api.PoolsApi._
import io.blockfrost.sdk.api.{PoolsApi, PoolsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class PoolsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  trait TestContext {
    val api: PoolsApi[Future, Any]
    val env: String
    val poolId: String
  }

  val testnetTestContext: TestContext = new TestContext {
    val api: PoolsApi[Future, Any] = new PoolsApiImpl[Future, Any] with TestnetApiClient
    val env: String = TestnetEnv
    val poolId: String = "pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w"
  }

  val mainnetTestContext: TestContext = new TestContext {
    val api: PoolsApi[Future, Any] = new PoolsApiImpl[Future, Any] with MainnetApiClient
    val env: String = MainnetEnv
    val poolId: String = "pool1g60m45m23f5vta30x5z7e0n2gc02yc4wyz6darfeluy2kgu65fa"
  }

  Seq(testnetTestContext, mainnetTestContext).foreach { ctx =>
    implicit val testCtx: TestContext = ctx

    s"getListOfStakePools [${ctx.env}]" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListOfStakePools(SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head.nonEmpty shouldBe true
          succeed
        })
    }

    s"getListOfRetiredStakePools [${ctx.env}]" should "return sequence of PoolContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListOfRetiredStakePools(SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head should matchPattern { case PoolContent(id, epoch) if id.nonEmpty && epoch >= 0 => }
          succeed
        })
    }

    s"getListOfRetiringStakePools [${ctx.env}]" should "return sequence of PoolContent" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getListOfRetiringStakePools(SortedPageRequest(1))
        .extract
        .map(body => {
          println(body)
          body.foreach(pool => pool should matchPattern { case PoolContent(id, epoch) if id.nonEmpty && epoch >= 0 => })
          succeed
        })
    }

    s"getSpecificStakePool [${ctx.env}]" should "return StakePool" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getSpecificStakePool(ctx.poolId)
        .extract
        .map(body => {
          body should matchPattern { case StakePool(id, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) if id.nonEmpty => }
          succeed
        })
    }

    s"getStakePoolHistory [${ctx.env}]" should "return sequence of PoolHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolHistory(ctx.poolId, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head should matchPattern { case PoolHistory(epoch, _, _, _, _, _, _) if epoch >= 0 => }
          succeed
        })
    }

    s"getStakePoolMetadata [${ctx.env}]" should "return sequence of PoolMetadata" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolMetadata(ctx.poolId)
        .extract
        .map(body => {
          body should matchPattern { case PoolMetadata(_, _, _, _, _, _, _, _) => }
          succeed
        })
    }

    s"getStakePoolRelays [${ctx.env}]" should "return sequence of PoolHistory" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolRelays(ctx.poolId)
        .extract
        .map(body => {
          body.foreach(pool => pool should matchPattern { case PoolRelay(_, _, _, _, _) => })
          succeed
        })
    }

    s"getStakePoolDelegators [${ctx.env}]" should "return sequence of PoolDelegator" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolDelegators(ctx.poolId, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head should matchPattern { case PoolDelegator(_, _) => }
          succeed
        })
    }

    s"getStakePoolBlocks [${ctx.env}]" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolBlocks(ctx.poolId, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 0
          succeed
        })
    }

    s"getStakePoolUpdates [${ctx.env}]" should "return sequence of PoolUpdate" in genericTestContext[TestContext] { ctx =>
      ctx.api
        .getStakePoolUpdates(ctx.poolId, SortedPageRequest(1))
        .extract
        .map(body => {
          body.size shouldBe 1
          body.head should matchPattern { case PoolUpdate(_, _, _) => }
          succeed
        })
    }
  }
}
