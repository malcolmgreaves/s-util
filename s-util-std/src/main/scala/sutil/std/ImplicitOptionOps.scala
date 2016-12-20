package sutil.std

object ImplicitOptionOps {

  /**
   * Adds same ability as "get" but with ability to customize
   * exception message.
   */
  implicit class GetUnsafeOpt[T](private val x: Option[T]) extends AnyVal {

    def getUnsafe(errMessage: => String = "Value not present"): T =
      x.fold { throw new IllegalAccessException(errMessage) } { identity }
  }

  /** Evaluates side effecting functions, values on the Some() and None cases. */
  implicit class AddSideEffectOpToOption[T](private val x: Option[T]) extends AnyVal {

    /**
     * Evaluates one of the two statements, depending on the case of the Option.
     * Performs fold(ifNone, ifSome) and then evaluates to the wrapped Option.
     */
    def sideEffectOnly(ifNone: => Unit, ifSome: T => Unit): Option[T] = {
      x.fold(ifNone)(ifSome)
      x
    }

    /** Evaluates the parameter when the Option is None. */
    def sideEffectOnly(ifNone: => Unit): Option[T] = {
      x.fold(ifNone)(_ => ())
      x
    }

    /** Evaluates the parameter on the inner value when the Option is Some(_). */
    def sideEffectOnly(ifSome: T => Unit): Option[T] = {
      x.fold(())(ifSome)
      x
    }
  }

}