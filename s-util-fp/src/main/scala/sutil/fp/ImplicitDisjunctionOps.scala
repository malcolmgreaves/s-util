package sutil.fp

import scalaz.\/
import Types.Err

/** Implicits that add a DSL for working with the scalaz.\/ type. */
object ImplicitDisjunctionOps {

  /** In both the left and right cases, runs a side-effecting unary function. */
  implicit class DoNextUnaryFn[A, B](private val disjunction: \/[A, B])
      extends AnyVal {

    /** Silently swallows error. */
    @inline def next(performImmediatelyAfter: () => Unit): \/[A, B] =
      disjunction
        .leftMap { error =>
          try { performImmediatelyAfter() } catch { case _: Throwable => () }
          error
        }
        .map { result =>
          try { performImmediatelyAfter() } catch { case _: Throwable => () }
          result
        }
  }

  /** In both the left and right cases, evaluates a side-effecting value. */
  implicit class DoNextLazy[A, B](private val disjunction: \/[A, B])
      extends AnyVal {

    /** Silently swallows error. */
    @inline def next(performImmediatelyAfter: => Any): \/[A, B] =
      disjunction
        .leftMap { error =>
          try { performImmediatelyAfter } catch { case _: Throwable => () }
          error
        }
        .map { result =>
          try { performImmediatelyAfter } catch { case _: Throwable => () }
          result
        }
  }

  /** Obtains the value within a disjunction. Throws an exception if the value is not present. */
  implicit class UnsafeGetOnDisjunction[T](private val x: Err[T])
      extends AnyVal {
    @inline def getUnsafe: T =
      x.fold(e => throw e, identity)
  }

  /** If the right value is an error, it propigates it to the left side. */
  implicit class FlattenOr[L, R](private val x: \/[L, \/[L, R]])
      extends AnyVal {
    @inline def flatten: \/[L, R] = x flatMap { identity }
  }

  /** Obtain the value from a disjunction of the same left and right types. */
  implicit class GetWhenUnified[T](private val x: \/[T, T]) extends AnyVal {
    @inline def get: T =
      x.fold(identity, identity)
  }

}
