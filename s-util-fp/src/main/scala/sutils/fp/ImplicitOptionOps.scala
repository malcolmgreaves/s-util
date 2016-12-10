package sutils.fp

import scalaz.{ -\/, \/, \/- }

/** Implicits that add a DSL for working with the Option type. */
object ImplicitOptionOps {

  /** Converts an Option[T] into a disjunction of Unit and T. */
  implicit class OptionToDisjunction[T](private val x: Option[T]) extends AnyVal {
    @inline def toOr: \/[Unit, T] =
      x match {
        case None        => -\/(())
        case Some(value) => \/-(value)
      }
  }

}