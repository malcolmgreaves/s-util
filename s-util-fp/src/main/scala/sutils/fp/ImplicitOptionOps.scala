package sutils.fp

import scalaz.{ -\/, \/, \/- }

/** Implicits that add a DSL for working with the Option type. */
object ImplicitOptionOps {

  /** Evaluates side effecting functions, values on the Some() and None cases. */
  implicit class AddSideEffectOpToOption[T](private val x: Option[T]) extends AnyVal {

    /**
     * Evaluates one of the two statements, depending on the case of the Option.
     * Performs fold(ifNone, ifSome) and then evaluates to the wrapped Option.
     */
    @inline def sideEffectOnly(ifNone: => Unit, ifSome: T => Unit): Option[T] = {
      x.fold(ifNone)(ifSome)
      x
    }

    /** Evaluates the parameter when the Option is None. */
    @inline def sideEffectOnly(ifNone: => Unit): Option[T] = {
      x.fold(ifNone)(_ => ())
      x
    }

    /** Evaluates the parameter on the inner value when the Option is Some(_). */
    @inline def sideEffectOnly(ifSome: T => Unit): Option[T] = {
      x.fold(())(ifSome)
      x
    }
  }

  /** Converts an Option[T] into a disjunction of Unit and T. */
  implicit class OptionToDisjunction[T](private val x: Option[T]) extends AnyVal {
    @inline def toOr: \/[Unit, T] =
      x match {
        case None => -\/(())
        case Some(value) => \/-(value)
      }
  }

}