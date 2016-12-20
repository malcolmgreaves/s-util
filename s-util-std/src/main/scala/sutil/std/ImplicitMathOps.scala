package sutil.std

object ImplicitMathOps {

  implicit class DoubleOps(private val x: Double) extends AnyVal {

    @inline def isEqual(other: Double): Boolean =
      MathHelpers.areEqual(x, other)

    @inline def isZero: Boolean =
      MathHelpers.isZero(x)

    @inline def isNaN: Boolean =
      java.lang.Double.isNaN(x)

  }

}
