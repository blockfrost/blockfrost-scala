package io.blockfrost.sdk

import io.blockfrost.sdk.api.PoolsApi._
import io.blockfrost.sdk.api.{PoolsApi, PoolsApiImpl}
import io.blockfrost.sdk.common.SortedPageRequest
import io.blockfrost.sdk.effect.FutureResponseConverter.FutureResponseOps
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class PoolsApiSpec extends AsyncFlatSpec with Matchers with TestContextSupport {
  "getListOfStakePools" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListOfStakePools(SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head.nonEmpty shouldBe true
        succeed
      })
  }

  "getListOfRetiredStakePools" should "return sequence of PoolContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListOfRetiredStakePools(SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head should matchPattern { case PoolContent(id, epoch) if id.nonEmpty && epoch >= 0 => }
        succeed
      })
  }

  "getListOfRetiringStakePools" should "return sequence of PoolContent" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getListOfRetiringStakePools(SortedPageRequest(1))
      .extract
      .map(body => {
        println(body)
        body.foreach(pool => pool should matchPattern { case PoolContent(id, epoch) if id.nonEmpty && epoch >= 0 => })
        succeed
      })
  }

  "getSpecificStakePool" should "return StakePool" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getSpecificStakePool("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w")
      .extract
      .map(body => {
        body should matchPattern { case StakePool(id, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) if id.nonEmpty => }
        succeed
      })
  }

  "getStakePoolHistory" should "return sequence of PoolHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolHistory("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w", SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head should matchPattern { case PoolHistory(epoch, _, _, _, _, _, _) if epoch >= 0 => }
        succeed
      })
  }

  "getStakePoolMetadata" should "return sequence of PoolMetadata" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolMetadata("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w")
      .extract
      .map(body => {
        body should matchPattern { case PoolMetadata(_, _, _, _, _, _, _, _) => }
        succeed
      })
  }

  "getStakePoolRelays" should "return sequence of PoolHistory" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolRelays("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w")
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head should matchPattern { case PoolRelay(_, _, _, _, _) => }
        succeed
      })
  }

  "getStakePoolDelegators" should "return sequence of PoolDelegator" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolDelegators("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w", SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head should matchPattern { case PoolDelegator("stake_test1uzaghtuxs0z569hnc68enjpnzy5tarqeg54k9p6rj5jaakq4dwqjg", _) => }
        succeed
      })
  }

  "getStakePoolBlocks" should "return sequence of strings" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolBlocks("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w", SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 0
        succeed
      })
  }

  "getStakePoolUpdates" should "return sequence of PoolUpdate" in genericTestContext[TestContext] { ctx =>
    ctx.api
      .getStakePoolUpdates("pool1adur9jcn0dkjpm3v8ayf94yn3fe5xfk2rqfz7rfpuh6cw6evd7w", SortedPageRequest(1))
      .extract
      .map(body => {
        body.size shouldBe 1
        body.head should matchPattern { case PoolUpdate(_, _, _) => }
        succeed
      })
  }

  trait TestContext {
    val api: PoolsApi[Future, Any] = new PoolsApiImpl[Future, Any] with TestnetApiClient
  }

  implicit val testContext: TestContext = new TestContext {}
}
