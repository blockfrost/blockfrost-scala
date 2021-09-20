package io.blockfrost.sdk.common

sealed trait PageRequest {
  def nextPage: PageRequest
  def nextPage(page: Int): PageRequest
}
case class SortedPageRequest(count: Int = 100, page: Int = 1, order: Order = Asc) extends PageRequest {
  def nextPage: SortedPageRequest = SortedPageRequest(count, page + 1, order)
  def nextPage(page: Int): SortedPageRequest = SortedPageRequest(count, page, order)
}
case class UnsortedPageRequest(count: Int = 100, page: Int = 1) extends PageRequest {
  def nextPage: UnsortedPageRequest = UnsortedPageRequest(count, page + 1)
  def nextPage(page: Int): UnsortedPageRequest = UnsortedPageRequest(count, page)
}
sealed trait Order {
  def get: String
}
case object Asc extends Order {
  def get: String = "asc"
}
case object Desc extends Order {
  def get: String = "desc"
}
