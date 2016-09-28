package sutils.fp

import org.scalatest.FunSuite

import Types._

import scalaz.{ -\/, \/, \/- }

class TypesTest extends FunSuite {

  test("Size constructors, invariants") {

    val positive = 631626
    val zero = 0
    val negative = -722174

    assert(Size(Size(positive)) === positive)
    assert(Size(Size(zero)) === zero)

    try {
      val _ = Size(negative)
      assert(false)
    } catch {
      case _: IllegalArgumentException => assert(true)
    }
  }

}
