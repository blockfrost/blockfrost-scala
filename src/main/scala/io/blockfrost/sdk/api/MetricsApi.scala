package io.blockfrost.sdk.api

import io.blockfrost.sdk.ApiClient
import io.blockfrost.sdk.api.MetricsApi.{EndpointMetric, Metric}
import io.blockfrost.sdk.common.{Config, SttpSupport}
import org.json4s.{Formats, Serialization}
import sttp.client3.UriContext

trait MetricsApi[F[_], P] extends SttpSupport {
  this: ApiClient[F, P] =>

  def getUsageMetrics(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Metric]]]

  def getEndpointUsageMetrics(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[EndpointMetric]]]
}

trait MetricsApiImpl[F[_], P] extends MetricsApi[F, P] {
  this: ApiClient[F, P] =>

  def getUsageMetrics(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[Metric]]] =
    get(uri"$host/metrics")

  def getEndpointUsageMetrics(implicit formats: Formats, serialization: Serialization, config: Config): F[ApiResponse[Seq[EndpointMetric]]] =
    get(uri"$host/metrics/endpoints")
}

object MetricsApi {
  case class Metric(time: Long, calls: Int)
  case class EndpointMetric(time: Long, calls: Int, endpoint: String)
}
