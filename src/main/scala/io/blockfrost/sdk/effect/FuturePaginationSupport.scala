package io.blockfrost.sdk.effect

import com.typesafe.scalalogging.LazyLogging
import io.blockfrost.sdk.common._
import sttp.client3.HttpError
import sttp.model.StatusCode

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

object FuturePaginationSupport extends LazyLogging {
  private type ApiRequest[A] = PageRequest => Future[Seq[A]]

  def getAllPages[A](request: ApiRequest[A], order: Option[Order])(implicit config: Config, ec: ExecutionContext): Future[Seq[A]] = {
    val pageRef = new AtomicInteger(0)
    val hasNext = new AtomicBoolean(true)
    val pageRequest: PageRequest = order match {
      case Some(ord) => SortedPageRequest(order = ord, page = 0)
      case None => UnsortedPageRequest(page = 0)
    }

    val workers = for (_ <- 1 to config.threadCount) yield {
      fetch(Seq.empty[A], hasNext, pageRef, request, pageRequest)
    }

    Future.sequence(workers).map(_.flatten)
  }

  private def fetch[A](accumulator: Seq[A], hasNext: AtomicBoolean, pageRef: AtomicInteger, request: ApiRequest[A], pageRequest: PageRequest)
                      (implicit ec: ExecutionContext, config: Config): Future[Seq[A]] = {
    request(pageRequest.nextPage(pageRef.incrementAndGet))
      .flatMap(handle(_, accumulator, hasNext, pageRef, request, pageRequest))
      .recoverWith {
        case e: HttpError[String] if e.statusCode == StatusCode.PaymentRequired =>
          logger.warn("Project's daily request limit reached")
          Future.successful(accumulator)
        case e: HttpError[String] if e.statusCode == StatusCode.TooManyRequests || e.statusCode == StatusCode(418) =>
          logger.debug(s"Rate limit reached, waiting ${config.rateLimitPauseMillis} millis")
          DelayedFuture(FiniteDuration.apply(config.rateLimitPauseMillis, TimeUnit.MILLISECONDS))(fetch(accumulator, hasNext, pageRef, request, pageRequest)).flatten
      }
  }

  private def handle[A](pageResult: Seq[A], accumulator: Seq[A], hasNext: AtomicBoolean, pageRef: AtomicInteger, request: ApiRequest[A], pageRequest: PageRequest)
                       (implicit ec: ExecutionContext, config: Config): Future[Seq[A]] = {
    val results = accumulator ++ pageResult
    if (pageResult.isEmpty || !hasNext.get) {
      if (hasNext.get) hasNext.set(false)
      Future.successful(results)
    } else {
      fetch(results, hasNext, pageRef, request, pageRequest)
    }
  }
}
