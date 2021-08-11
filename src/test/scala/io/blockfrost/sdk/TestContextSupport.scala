package io.blockfrost.sdk

import org.scalatest.Assertion

import scala.concurrent.Future

trait TestContextSupport {
  def genericTestContext[A](test: A => Future[Assertion])(implicit context: A): Future[Assertion] = {
    test(context)
  }
}
