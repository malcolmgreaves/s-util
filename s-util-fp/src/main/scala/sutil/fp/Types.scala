package sutil.fp

import scalaz.{ \/, Tag, @@ }

object Types {

  /** A value of type T that could have an associated error. */
  type Err[T] = Throwable \/ T

  /** Unit, with the possibility of an error associated with the side effect. */
  type SideEffect = Err[Unit]

  /** A non-negative integer type. */
  type Size = Int @@ Size.T
  object Size {
    /** Size constructor: throws Exception if input is negative. */
    def apply(x: Int): Size = {
      if (x >= 0) Tag[Int, T](x)
      else throw new IllegalArgumentException(s"Size must be non-negative, not $x")
    }
    /** Size value accessor. */
    def apply(x: Size): Int = Tag.unwrap(x)
    /** The Size phantom type. */
    sealed trait T
  }

}
