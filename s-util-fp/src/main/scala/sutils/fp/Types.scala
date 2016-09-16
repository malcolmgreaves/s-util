package sutils.fp

import scalaz.\/

object Types {

  /** A value of type T that could have an associated error. */
  type Err[T] = Throwable \/ T

  /** Unit, with the possibility of an error associated with the side effect. */
  type SideEffect = Err[Unit]

}