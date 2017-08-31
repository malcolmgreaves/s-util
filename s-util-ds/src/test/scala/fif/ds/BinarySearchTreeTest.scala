package fif.ds

import org.scalatest.FunSuite

import scala.util.Random

class BinarySearchTreeTest extends FunSuite {

  private val rand = new Random()

  implicit val ic = Cmp.numeric[Int]

  val bstInt = BinarySearchTree[Int]()

  test("simple cases: sort empty sequence, sequence of 1, sequence of 3") {
    val sequencesToSort = Seq(
      Seq.empty[Int],
      Seq(1),
      Seq(3, 1, 2)
    )
    val expected = Seq(
      Seq.empty[Int],
      Seq(1),
      Seq(1, 2, 3)
    )
    sequencesToSort
      .zip(expected)
      .foreach {
        case (seq, expect) =>
          val inserted = UnboundedContainer.insert(bstInt, seq: _*)
          val result = bstInt.sort(inserted)
          assert(result == expect)
      }
  }

  test("contains in a tree") {
    val values = Seq(1, 2, 3, 4, 5)
    val tree = UnboundedContainer.insert(bstInt, values: _*)
    values.foreach { v =>
      assert(bstInt.contains(v)(tree))
    }
    Seq(-10, -1, 0, 44, 55).foreach { v =>
      assert(!bstInt.contains(v)(tree))
    }
  }

  test("delete elements from tree") {

    val tree = UnboundedContainer.insert(bstInt, 1, 2, 3, 4, 5)
    bstInt.delete(3)(tree) match {

      case None =>
        fail(s"expecting to remove 3 from tree: $tree")

      case Some(newTree) =>
        assert(!bstInt.contains(3)(newTree))
    }

    bstInt.delete(-1)(tree) match {
      case None =>
        () // expected !

      case Some(shouldNotHaveDeleted) =>
        fail(
          s"should not have delted non existant element from tree, result: $shouldNotHaveDeleted")
    }

    bstInt.delete(5)(tree) match {
      case None =>
        fail(s"expecting to remove 5 from tree: $tree")

      case Some(deleted) =>
        assert(
          bstInt.sort(deleted) == bstInt.sort(
            UnboundedContainer.insert(bstInt, 1, 2, 3, 4)))
    }

    bstInt.delete(1)(tree) match {
      case None =>
        fail(s"expecting to remove 1 from tree: $tree")

      case Some(deleted) =>
        assert(
          bstInt.sort(deleted) == bstInt.sort(
            UnboundedContainer.insert(bstInt, 2, 3, 4, 5)))
    }
  }

  val nElementsForRand = 1000

  test("merge equivalent trees") {
    val t1 = UnboundedContainer.insert(bstInt, 1, 2, 3)
    val t2 = UnboundedContainer.insert(bstInt, 1, 2, 3)
    val merged = bstInt.merge(t1, t2)
    println(s"EQUIV:\n$merged\n")
    val expected = Seq(1, 1, 2, 2, 3, 3)
    assert(expected == bstInt.sort(merged))
  }

  test("merge trees, edge cases") {
    import TreeParts._
    val t1 = UnboundedContainer.insert(bstInt, 1, 2, 3)
    val expected = Seq(1, 2, 3)
    assert(expected == bstInt.sort(bstInt.merge(t1, Empty)))
    assert(expected == bstInt.sort(bstInt.merge(Empty, t1)))
    assert(Seq.empty[Int] == bstInt.sort(bstInt.merge(Empty, Empty)))
  }

  test(s"sort list of $nElementsForRand numbers drawn uniformly at random") {
    val elements = (0 until nElementsForRand).map(_ => rand.nextInt(100)).toSeq
    val expected = elements.sorted

    val result = bstInt.sort(UnboundedContainer.insert(bstInt, elements: _*))
    assert(result == expected)
  }

  val randStrLen = 20

  test(
    s"sort list of $nElementsForRand strings length $randStrLen drawn uniformly at random") {
    val elements =
      (0 until nElementsForRand).map(_ => rand.nextString(randStrLen)).toSeq
    val expected = elements.sorted
    implicit val sc = Cmp.Implicits.strCmp
    val bstStr = BinarySearchTree[String]()
    val result = bstStr.sort(UnboundedContainer.insert(bstStr, elements: _*))
    assert(result == expected)
  }

}
