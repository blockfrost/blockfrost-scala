package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.PoolsApi._
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait PoolsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getListOfStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[String]]]

  def getListOfRetiredStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolContent]]]

  def getListOfRetiringStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolContent]]]

  def getSpecificStakePool(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[StakePool]]

  def getStakePoolHistory(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolHistory]]]

  def getStakePoolMetadata(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PoolMetadata]]

  def getStakePoolRelays(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolRelay]]]

  def getStakePoolDelegators(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolDelegator]]]

  def getStakePoolBlocks(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[String]]]

  def getStakePoolUpdates(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolUpdate]]]
}

trait PoolsApiImpl[F[_], P] extends PoolsApi[F, P] {
  this: ApiClient[F, P] =>

  override def getListOfStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[String]]] =
    get(uri"$host/pools", Some(pageRequest))

  override def getListOfRetiredStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolContent]]] =
    get(uri"$host/pools/retired", Some(pageRequest))

  override def getListOfRetiringStakePools(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolContent]]] =
    get(uri"$host/pools/retiring", Some(pageRequest))

  override def getSpecificStakePool(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[StakePool]] =
    get(uri"$host/pools/$poolId")

  override def getStakePoolHistory(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolHistory]]] =
    get(uri"$host/pools/$poolId/history", Some(pageRequest))

  override def getStakePoolMetadata(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[PoolMetadata]] =
    get(uri"$host/pools/$poolId/metadata")

  override def getStakePoolRelays(poolId: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolRelay]]] =
    get(uri"$host/pools/$poolId/relays")

  override def getStakePoolDelegators(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolDelegator]]] =
    get(uri"$host/pools/$poolId/delegators", Some(pageRequest))

  override def getStakePoolBlocks(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[String]]] =
    get(uri"$host/pools/$poolId/blocks", Some(pageRequest))

  override def getStakePoolUpdates(poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[PoolUpdate]]] =
    get(uri"$host/pools/$poolId/updates", Some(pageRequest))
}

object PoolsApi {
  case class PoolContent(pool_id: String, epoch: Int)
  case class StakePool(pool_id: String,
                       hex: String,
                       vrf_key: String,
                       blocks_minted: Int,
                       live_stake: String,
                       live_size: Double,
                       live_saturation: Double,
                       live_delegators: Int,
                       active_stake: String,
                       active_size: Double,
                       declared_pledge: String,
                       live_pledge: String,
                       margin_cost: Double,
                       fixed_cost: String,
                       reward_account: String,
                       owners: Seq[String],
                       registration: Seq[String],
                       retirement: Seq[String])
  case class PoolHistory(epoch: Int,
                         blocks: Int,
                         active_stake: String,
                         active_size: Double,
                         delegators_count: Int,
                         rewards: String,
                         fees: String)
  case class PoolMetadata(pool_id: Option[String],
                          hex: Option[String],
                          url: Option[String],
                          hash: Option[String],
                          ticker: Option[String],
                          name: Option[String],
                          description: Option[String],
                          homepage: Option[String])
  case class PoolRelay(ipv4: Option[String], ipv6: Option[String], dns: Option[String], dns_srv: Option[String], port: Int)
  case class PoolDelegator(address: String, live_stake: String)
  case class PoolUpdate(tx_hash: String, cert_index: Int, action: String)
}
