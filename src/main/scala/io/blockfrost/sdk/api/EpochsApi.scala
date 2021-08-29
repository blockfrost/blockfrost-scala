package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.EpochsApi.{Epoch, EpochProtocolParameters, PoolStakeDistribution, StakeDistribution}
import io.blockfrost.sdk.common.{SortedPageRequest, SttpSupport, UnsortedPageRequest}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait EpochsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getLatestEpoch(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Epoch]]

  def getLatestEpochProtocolParameters(implicit formats: Formats, serialization: Serialization): F[ApiResponse[EpochProtocolParameters]]

  def getSpecificEpoch(number: Int)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Epoch]]

  def getListingOfNextEpochs(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Epoch]]]

  def getListingOfPreviousEpochs(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Epoch]]]

  def getStakeDistribution(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[StakeDistribution]]]

  def getStakeDistributionByPool(number: Int, poolId: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[PoolStakeDistribution]]]

  def getBlockDistribution(number: Int, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]]

  def getBlockDistributionByPool(number: Int, poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]]

  def getProtocolParameters(number: Int)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[EpochProtocolParameters]]
}

trait EpochsApiImpl[F[_], P] extends EpochsApi[F, P] {
  this: ApiClient[F, P] =>

  override def getLatestEpoch(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Epoch]] =
    get(uri"$host/epochs/latest")

  override def getLatestEpochProtocolParameters(implicit formats: Formats, serialization: Serialization): F[ApiResponse[EpochProtocolParameters]] =
    get(uri"$host/epochs/latest/parameters")

  override def getSpecificEpoch(number: Int)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Epoch]] =
    get(uri"$host/epochs/$number")

  override def getListingOfNextEpochs(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Epoch]]] =
    get(uri"$host/epochs/$number/next", Some(pageRequest))

  override def getListingOfPreviousEpochs(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Epoch]]] =
    get(uri"$host/epochs/$number/previous", Some(pageRequest))

  override def getStakeDistribution(number: Int, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[StakeDistribution]]] =
    get(uri"$host/epochs/$number/stakes", Some(pageRequest))

  override def getStakeDistributionByPool(number: Int, poolId: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[PoolStakeDistribution]]] =
    get(uri"$host/epochs/$number/stakes/$poolId", Some(pageRequest))

  override def getBlockDistribution(number: Int, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]] =
    get(uri"$host/epochs/$number/blocks", Some(pageRequest))

  override def getBlockDistributionByPool(number: Int, poolId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]] =
    get(uri"$host/epochs/$number/blocks/$poolId", Some(pageRequest))

  override def getProtocolParameters(number: Int)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[EpochProtocolParameters]] =
    get(uri"$host/epochs/$number/parameters")
}

object EpochsApi {
  case class Epoch(epoch: Int,
                   start_time: Int,
                   end_time: Int,
                   first_block_time: Int,
                   last_block_time: Int,
                   block_count: Int,
                   tx_count: Int,
                   output: String,
                   fees: String,
                   active_stake: String)
  case class EpochProtocolParameters(epoch: Int,
                                     min_fee_a: Int,
                                     min_fee_b: Int,
                                     max_block_size: Int,
                                     max_tx_size: Int,
                                     max_block_header_size: Int,
                                     key_deposit: String,
                                     pool_deposit: String,
                                     e_max: Int,
                                     n_opt: Int,
                                     a0: Double,
                                     rho: Double,
                                     tau: Double,
                                     decentralisation_param: Double,
                                     extra_entropy: String,
                                     protocol_major_ver: Int,
                                     protocol_minor_ver: Int,
                                     min_utxo: String,
                                     min_pool_cost: String,
                                     nonce: String)
  case class StakeDistribution(stake_address: String, pool_id: String, amount: String)
  case class PoolStakeDistribution(stake_address: String, amount: String)
}
