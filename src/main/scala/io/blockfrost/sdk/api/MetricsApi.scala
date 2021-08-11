package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.MetricsApi.{EndpointMetric, Metric}
import io.blockfrost.sdk.common.SttpSupport
import org.json4s.{Formats, Serialization}

trait MetricsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getUsageMetrics(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[Metric]]]

  def getEndpointUsageMetrics(implicit formats: Formats, serialization: Serialization): F[ApiResponse[Seq[EndpointMetric]]]
}

object MetricsApi {
  case class Metric(time: Long, calls: Int)
  case class EndpointMetric(time: Long, calls: Int, endpoint: String)
}
