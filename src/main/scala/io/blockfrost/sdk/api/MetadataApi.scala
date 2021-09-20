package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.MetadataApi.{MetadataContentCbor, MetadataContentJson, MetadataLabel}
import io.blockfrost.sdk.common.{Config, SortedPageRequest, SttpSupport}
import org.json4s.JsonAST.JValue
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait MetadataApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getTransactionMetadataLabels(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataLabel]]]

  def getTransactionMetadataContentJson(label: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataContentJson]]]

  def getTransactionMetadataContentCbor(label: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataContentCbor]]]
}

trait MetadataApiImpl[F[_], P] extends MetadataApi[F, P] {
  this: ApiClient[F, P] =>

  override def getTransactionMetadataLabels(pageRequest: SortedPageRequest)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataLabel]]] =
    get(uri"$host/metadata/txs/labels", Some(pageRequest))

  override def getTransactionMetadataContentJson(label: String, pageRequest: SortedPageRequest)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataContentJson]]] =
    get(uri"$host/metadata/txs/labels/$label", Some(pageRequest))

  override def getTransactionMetadataContentCbor(label: String, pageRequest: SortedPageRequest)(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[MetadataContentCbor]]] =
    get(uri"$host/metadata/txs/labels/$label/cbor", Some(pageRequest))
}

object MetadataApi {
  case class MetadataLabel(label: String, cip10: String, count: String)
  case class MetadataContentJson(tx_hash: String, json_metadata: JValue)
  case class MetadataContentCbor(tx_hash: String, cbor_metadata: String)
}
