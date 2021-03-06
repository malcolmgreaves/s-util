package sutil.fp

import org.scalatest.FunSuite

import Types._

import scala.util.{ Failure, Success, Try }
import scalaz.{ -\/, \/, \/- }

class ImplicitOpsTest extends FunSuite {

  test("""Disjunction (scalaz.\/) DSL""") {
    import ImplicitDisjunctionOps._

    // next
    val _0 = \/-("Hello")
      .next { () => assert(true) }
    val _1 = \/-("Hello")
      .next { assert(true) }

    // getUnsafe
    assert("Hello" === \/-("Hello").getUnsafe)
    try {
      val e = new Exception()
      -\/(e).getUnsafe
    } catch {
      case e: Exception => assert(true)
    }

    // flaten
    {
      var nested: Int \/ (Int \/ String) = -\/(0)
      var unnested: Int \/ String = -\/(0)
      assert(nested.flatten == unnested)

      nested = \/-(-\/(10))
      unnested = -\/(10)
      assert(nested.flatten == unnested)

      nested = \/-(\/-("hello world"))
      unnested = \/-("hello world")
      assert(nested.flatten == unnested)
    }

    // get
    {
      var orHelloWorld: String \/ String = \/-("world")
      assert("world" === orHelloWorld.get)
      orHelloWorld = -\/("hello")
      assert("hello" === orHelloWorld.get)
    }
  }

  test("""Option DSL""") {
    import ImplicitOptionOps._

    val someHello: Option[String] = Some("hello")

    // toOr
    var optVal: Option[String] = someHello
    assert(optVal.toOr == \/-("hello"))

    optVal = None
    assert(optVal.toOr == -\/(()))
  }

  test("""Try DSL""") {
    import ImplicitTryOps._

    var x: Try[String] = Success("hello")
    assert(x.toOr == \/-("hello"))

    val e = new Exception()
    x = Failure(e)
    assert(x.toOr == -\/(e))
  }

  test("""Misc / Extra / Unorganized Types DSL""") {
    import ImplicitMiscOps._

    val value = "hello world"

    val shouldBeValue = value
      .sideEffect { () => assert(true) }
      .sideEffect { assert(true) }

    assert(shouldBeValue === value)
  }

}