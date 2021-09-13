package io.blockfrost.sdk.common

sealed trait PageRequest
case class SortedPageRequest(count: Int = 100, page: Int = 1, order: String = "asc") extends PageRequest
case class UnsortedPageRequest(count: Int = 100, page: Int = 1) extends PageRequest
