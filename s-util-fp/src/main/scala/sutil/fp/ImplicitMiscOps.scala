package sutil.fp

import scalaz.{-\/, \/, \/-}

object ImplicitMiscOps {

  /** Evaluate a unary side-effecting function. Convenient for method chaining on a value of type T. */
  implicit class ChainSideEffectUnaryFn[T](private val x: T) extends AnyVal {
    @inline def sideEffect(op: () => Unit): T = {
      op()
      x
    }
  }

  /** Evaluate a side-effecting value. Convenient for method chaining on a value of type T. */
  implicit class ChainSideEffectLazy[T](private val x: T) extends AnyVal {
    @inline def sideEffect(op: => Unit): T = {
      val _: Unit = op // force eager evaluation
      x
    }
  }

}
