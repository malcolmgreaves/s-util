package sutil.std

import scala.util.{Try, Success, Failure}

object ImplicitTryOps {

  implicit class FoldTry[T](private val x: Try[T]) extends AnyVal {

    def fold[R](ifFailure: Throwable => R, ifSuccess: T => R): R =
      x match {
        case Failure(e) => ifFailure(e)
        case Success(t) => ifSuccess(t)
      }
  }

  implicit class AddSideEffectOpToTry[T](private val x: Try[T])
      extends AnyVal {

    /**
      * Evaluates one of the two statements, depending on the case of the Try.
      * Performs fold(ifFailure, ifSuccess) and then evaluates to the wrapped Try.
      */
    def effect(
        ifFailure: Throwable => Unit,
        ifSuccess: T => Unit
    ): Try[T] = {
      val _ = x.fold(ifFailure, ifSuccess)
      x
    }

    /** Evaluates the parameter when the Try is Failure(_). */
    def effectOnFailure(ifFailure: Throwable => Unit): Try[T] = {
      val _ = x.fold(ifFailure, _ => ())
      x
    }

    /** Evaluates the parameter on the inner value when the Try is Success(_). */
    def effectOnSuccess(ifSuccess: T => Unit): Try[T] = {
      val _ = x.fold(_ => (), ifSuccess)
      x
    }

  }

}
