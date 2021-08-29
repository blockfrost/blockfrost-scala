package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.AssetsApi._
import io.blockfrost.sdk.common.{SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait AssetsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getAssets(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetShort]]]

  def getSpecificAsset(asset: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Asset]]

  def getAssetHistory(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetHistory]]]

  def getAssetTransactions(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetTransaction]]]

  def getAssetAddresses(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetAddress]]]

  def getAssetOfSpecificPolicy(policyId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetShort]]]
}

trait AssetsApiImpl[F[_], P] extends AssetsApi[F, P] {
  this: ApiClient[F, P] =>

  override def getAssets(pageRequest: SortedPageRequest)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetShort]]] =
    get(uri"$host/assets", Some(pageRequest))

  override def getSpecificAsset(asset: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Asset]] =
    get(uri"$host/assets/$asset")

  override def getAssetHistory(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetHistory]]] =
    get(uri"$host/assets/$asset/history", Some(pageRequest))

  override def getAssetTransactions(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetTransaction]]] =
    get(uri"$host/assets/$asset/transactions", Some(pageRequest))

  override def getAssetAddresses(asset: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetAddress]]] =
    get(uri"$host/assets/$asset/addresses", Some(pageRequest))

  override def getAssetOfSpecificPolicy(policyId: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[AssetShort]]] =
    get(uri"$host/assets/policy/$policyId", Some(pageRequest))
}

object AssetsApi {
  case class AssetShort(asset: String, quantity: String)
  case class Metadata(name: String, description: String, ticker: String, url: String, logo: String, decimals: Int)
  case class OnchainMetadata(name: String, image: String)
  case class Asset(asset: String,
                   policy_id: String,
                   asset_name: String,
                   fingerprint: String,
                   quantity: String,
                   initial_mint_tx_hash: String,
                   mint_or_burn_count: Int,
                   onchain_metadata: Option[OnchainMetadata],
                   metadata: Option[Metadata])
  case class AssetHistory(tx_hash: String, amount: String, action: String)
  case class AssetTransaction(tx_hash: String, tx_index: Int, block_height: Int)
  case class AssetAddress(address: String, quantity: String)
}
