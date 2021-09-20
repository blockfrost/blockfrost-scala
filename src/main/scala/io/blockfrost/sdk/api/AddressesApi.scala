package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.AddressesApi.{Address, AddressDetail, Transaction, Utxo}
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext
import sttp.model.Uri.QuerySegment.{KeyValue, Value}

trait AddressesApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getSpecificAddress(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Address]]

  def getAddressDetails(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AddressDetail]]

  def getAddressUtxos(address: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Utxo]]]

  def getAddressTransactions(address: String, from: Option[String], to: Option[String], pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Transaction]]]
}

trait AddressesApiImpl[F[_], P] extends AddressesApi[F, P] {
  this: ApiClient[F, P] =>

  override def getSpecificAddress(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Address]] =
    get(uri"$host/addresses/$address")

  override def getAddressDetails(address: String)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[AddressDetail]] =
    get(uri"$host/addresses/$address/total")

  override def getAddressUtxos(address: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Utxo]]] =
    get(uri"$host/addresses/$address/utxos", Some(pageRequest))

  override def getAddressTransactions(address: String, from: Option[String], to: Option[String], pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Transaction]]] = {
    get(
      uri"$host/addresses/$address/transactions"
        .addQuerySegment(from.map(KeyValue("from", _)).getOrElse(Value("")))
        .addQuerySegment(to.map(KeyValue("to", _)).getOrElse(Value(""))),
      Some(pageRequest)
    )
  }
}

object AddressesApi {
  case class Transaction(tx_hash: String, tx_index: Int, block_height: Int)
  case class Utxo(tx_hash: String, output_index: Int, amount: Seq[Amount], block: String)
  case class AddressDetail(address: String, received_sum: Seq[Amount], sent_sum: Seq[Amount], tx_count: Int)
  case class Address(address: String, amount: Seq[Amount], stake_address: String, `type`: String)
  case class Amount(unit: String, quantity: String)
}
