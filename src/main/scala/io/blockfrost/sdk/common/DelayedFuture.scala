package io.blockfrost.sdk.common

import java.util.{Timer, TimerTask}
import scala.concurrent._
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object DelayedFuture {
  private val timer = new Timer(true)

  private def makeTask[T](body: => T)(schedule: TimerTask => Unit)(implicit ctx: ExecutionContext): Future[T] = {
    val prom = Promise[T]()
    schedule(
      new TimerTask {
        def run(): Unit = {
          ctx.execute(() => {
            prom.complete(Try(body))
          })
        }
      }
    )
    prom.future
  }

  def apply[T](delay: FiniteDuration)(body: => T)(implicit ctx: ExecutionContext): Future[T] =
    makeTask(body)(timer.schedule(_, delay.toMillis))
}
