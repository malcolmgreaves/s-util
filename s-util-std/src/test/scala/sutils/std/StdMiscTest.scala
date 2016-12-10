package sutils.std

import org.scalatest.FunSuite

class StdMiscTest extends FunSuite {

  test("Extended Option syntax") {

    import ImplicitOptionOps._

    // get unsafe

    assert(Some("hello world").getUnsafe() === "hello world")

    try {
      val x: Option[Boolean] = None
      val _ = x.getUnsafe()
      assert(false)
    } catch {
      case e: IllegalAccessException =>
        assert(e.getMessage === "Value not present")
    }

    try {
      val x: Option[Boolean] = None
      val _ = x.getUnsafe("Oh no! :*(")
      assert(false)
    } catch {
      case e: IllegalAccessException =>
        assert(e.getMessage === "Oh no! :*(")
    }

    // sideEffectOnly

    var optVal: Option[String] = Some("hello")
    val someHello = optVal
      .sideEffectOnly { assert(false) }
      .sideEffectOnly { v => assert("hello" === v) }
      .sideEffectOnly(
        assert(false),
        v => assert("hello" === v)
      )
    assert(optVal === someHello)

    optVal = None
    val none = optVal
      .sideEffectOnly { assert(true) }
      .sideEffectOnly { _ => assert(false) }
      .sideEffectOnly(
        assert(true),
        _ => assert(false)
      )
    assert(optVal === none)
  }

  test("String helpers") {
    import StringHelpers._

    val result = Seq("hello", "world", "how", "are", "you", "today")

    // split
    assert(result === split(" ")("hello world how are you today"))
    assert(result === lcaseSpaceSplit("HEllO wOrlD hOW ARE you ToDaY"))

    // insert

    val fromWorld = Seq("world", "how", "are", "you", "today")
    assert(result === insert(fromWorld, 0, "hello"))
    assert(result === prepend(fromWorld, "hello"))

    val toYou = Seq("hello", "world", "how", "are", "you")
    assert(result === insert(toYou, toYou.size, "today"))
    assert(result === append(toYou, "today"))

    assert {
      result === insert(Seq("hello", "how", "are", "you", "today"), 1, "world")
    }
    assert {
      result === insert(Seq("hello", "world", "are", "you", "today"), 2, "how")
    }
    assert {
      result === insert(Seq("hello", "world", "how", "you", "today"), 3, "are")
    }
    assert {
      result === insert(Seq("hello", "world", "how", "are", "today"), 4, "you")
    }
  }

  test("Distribution") {
    import Distribution._
    import MathHelpers._
    implicit val _ = new java.util.Random(System.currentTimeMillis)

    // uniform
    {
      val u = uniform(10, 2)
      assert(u.length == 10)
      u.foreach { x =>
        assert(x >= 0.0 && x <= 2.0)
      }
    }

    // gaussian
    {
      val g = gaussian(5, 1)(1000)
      assert(g.length == 1000)

      val mean = g.foldLeft(0.0) { _ + _ } / g.length
      assert(areEqual(mean, 5.0, 0.1))

      val variance = g.foldLeft(0.0) {
        case (sum, next) =>
          val diff = next - mean
          sum + (diff * diff)
      } / (g.length - 1)
      val stdDev = math.sqrt(variance)
      assert(areEqual(stdDev, 1.0, 0.1))
    }

  }

  test("IO helpers") {
    import IoHelpers._
    // output to a file

    outputToFile(new java.io.File("/dev"), "null")(Seq("hello")) match {
      case scala.util.Success(()) => assert(true)
      case scala.util.Failure(e)  => throw e
    }
  }

}