package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.NutlinkApi.{AddressMetadata, Ticker, TickerRecord}
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.JsonAST.JValue
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait NutlinkApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def listMetadata(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AddressMetadata]]

  def listTickers(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Ticker]]]

  def listAddressTickerRecords(address: String, ticker: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[TickerRecord]]]

  def listTickerRecords(ticker: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[TickerRecord]]]
}

trait NutlinkApiImpl[F[_], P] extends NutlinkApi[F, P] {
  this: ApiClient[F, P] =>

  override def listMetadata(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AddressMetadata]] =
    get(uri"$host/nutlink/$address")

  override def listTickers(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Ticker]]] =
    get(uri"$host/nutlink/$address/tickers")

  override def listAddressTickerRecords(address: String, ticker: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[TickerRecord]]] =
    get(uri"$host/nutlink/$address/tickers/$ticker", Some(pageRequest))

  override def listTickerRecords(ticker: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[TickerRecord]]] =
    get(uri"$host/nutlink/tickers/$ticker", Some(pageRequest))
}

object NutlinkApi {
  case class AddressMetadata(address: String, metadata_url: String, metadata_hash: String, metadata: Option[Map[String, String]])
  case class Ticker(name: String, count: Double, latest_block: Double)
  case class TickerRecord(address: Option[String], tx_hash: String, block_height: Double, tx_index: Double, payload: JValue)
}
