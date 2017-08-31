package fif.ds

import org.scalatest.FunSuite

import scala.annotation.tailrec
import scala.language.postfixOps

class MinHeapTest extends FunSuite {

  implicit val intCmp = Cmp.numeric[Int]

  test("Simple min heap of size 1") {

    val bounded1MinHeap = MinHeap.Bounded[Int](1)

    val (result, _) =
      BoundedContainer.insert(bounded1MinHeap)(bounded1MinHeap.empty, 3, 4, 1)

    val (min, rest) = bounded1MinHeap.takeMin(result).get

    assert(min == 1, "expected min to be 1")
    assert(bounded1MinHeap.takeMin(rest).isEmpty,
           "expected bounded 1 PQ to only have 1 element")
  }

  test("Use unbounded min heap to sort ") {
    val values = Seq(9, 5, 10, 11, 24, 4, 3, 8, 4, 1, 2)

    val UnboundedMinHeap = MinHeap.Unbounded[Int]

    val result =
      UnboundedContainer.insert(UnboundedMinHeap)(UnboundedMinHeap.empty,
                                                  values: _*)

    @tailrec @inline def check(minSortedValues: List[Int],
                               e: UnboundedMinHeap.Structure): Unit =
      UnboundedMinHeap.takeMin(e) match {

        case None =>
          assert(minSortedValues.isEmpty,
                 "heap empty, expecting value list to be as well")

        case Some((min, restOfHeap)) =>
          minSortedValues match {

            case Nil =>
              fail("unexpected: ran out of values but still more in heap")

            case head :: tail =>
              assert(
                head == min,
                s"expecting sorted values to match heap takeMin... have $min expecting $head"
              )

              check(tail, restOfHeap)

          }
      }

    check(values.sorted.toList, result)
  }

  test("Min heap of size 3") {
    val values = Seq(9, 5, 10, 11, 24, 4, 3, 8, 4, 1, 2)

    val BoundValuePQ = MinHeap.Bounded[Int](3)

    val (resultPq, removed) =
      BoundedContainer.insert(BoundValuePQ)(BoundValuePQ.empty, values: _*)
    assert(removed.isDefined && removed.get.size == values.size - 3)

    val (item1, pq1) = BoundValuePQ.takeMin(resultPq).get

    assert(item1 == 1)

    val (item2, pq2) = BoundValuePQ.takeMin(pq1).get
    assert(item2 == 2)

    val (item3, pq3) = BoundValuePQ.takeMin(pq2).get

    assert(item3 == 3)

    assert(BoundValuePQ.takeMin(pq3).isEmpty)
  }

}
