package sutil.std

object MathHelpers {

  val defaultDoubleEqualityTol = 1e-8

  @inline
  def areEqual(
      a: Double,
      b: Double,
      tolerance: Double = defaultDoubleEqualityTol
  ): Boolean =
    math.abs(a - b) < tolerance

  @inline
  def isZero(
      y: Double,
      tolerance: Double = defaultDoubleEqualityTol
  ): Boolean =
    math.abs(y) < tolerance

}
