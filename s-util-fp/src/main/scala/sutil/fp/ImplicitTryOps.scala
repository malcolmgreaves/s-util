package sutil.fp

import scala.util.{Failure, Success, Try}
import scalaz.{-\/, \/, \/-}

object ImplicitTryOps {

  implicit class TryToDisjunction[T](private val x: Try[T]) extends AnyVal {
    @inline def toOr: \/[Throwable, T] =
      x match {
        case Success(value) => \/-(value)
        case Failure(e) => -\/(e)
      }
  }

}
