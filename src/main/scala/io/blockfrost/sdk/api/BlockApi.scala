package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.BlockApi.BlockContent
import io.blockfrost.sdk.common.{SortedPageRequest, SttpSupport, UnsortedPageRequest}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait BlockApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getLatestBlock(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]]

  def getLatestBlockTransactions(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]]

  def getSpecificBlock(hashOrNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]]

  def getSpecificBlockInSlot(slotNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]]

  def getSpecificBlockInSlotInEpoch(slotNumber: String, epochNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]]

  def getListingOfNextBlocks(hashOrNumber: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[BlockContent]]]

  def getListingOfPreviousBlocks(hashOrNumber: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[BlockContent]]]

  def getBlockTransactions(hashOrNumber: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]]
}

trait BlockApiImpl[F[_], P] extends BlockApi[F, P] {
  this: ApiClient[F, P] =>

  override def getLatestBlock(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]] =
    get(uri"$host/blocks/latest")

  override def getLatestBlockTransactions(pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]] =
    get(uri"$host/blocks/latest/txs", Some(pageRequest))

  override def getSpecificBlock(hashOrNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]] =
    get(uri"$host/blocks/$hashOrNumber")

  override def getSpecificBlockInSlot(slotNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]] =
    get(uri"$host/blocks/slot/$slotNumber")

  override def getSpecificBlockInSlotInEpoch(slotNumber: String, epochNumber: String)(implicit formats: Formats, serialization: Serialization): F[ApiResponse[BlockContent]] =
    get(uri"$host/blocks/epoch/$epochNumber/slot/$slotNumber")

  override def getListingOfNextBlocks(hashOrNumber: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[BlockContent]]] =
    get(uri"$host/blocks/$hashOrNumber/next", Some(pageRequest))

  override def getListingOfPreviousBlocks(hashOrNumber: String, pageRequest: UnsortedPageRequest = UnsortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[BlockContent]]] =
    get(uri"$host/blocks/$hashOrNumber/previous", Some(pageRequest))

  override def getBlockTransactions(hashOrNumber: String, pageRequest: SortedPageRequest = SortedPageRequest())(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[String]]] =
    get(uri"$host/blocks/$hashOrNumber/txs", Some(pageRequest))
}

object BlockApi {
  case class BlockContent(time: Int,
                          height: Int,
                          hash: String,
                          slot: Int,
                          epoch: Int,
                          epoch_slot: Int,
                          slot_leader: String,
                          size: Int,
                          tx_count: Int,
                          output: String,
                          fees: String,
                          block_vrf: String,
                          previous_block: String,
                          next_block: String,
                          confirmations: Int)
}
