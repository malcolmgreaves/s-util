package fif.ds

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.language.{ postfixOps, higherKinds }

abstract class MinHeap[A: Cmp]
    extends SortableContainer[A, TreeParts.Tree[A]]
    with TreeLikeContainer[A]
    with FastMin[A, TreeParts.Tree[A]] {

  type Bounded = MinHeap[A] with BoundedContainer[A, this.Structure]
  type Unbounded = MinHeap[A] with UnboundedContainer[A, this.Structure]

  override val cmp = implicitly[Cmp[A]]

  override def peekMin(existing: Structure): Option[A] =
    existing.map(identity[A])

  override def sort(existing: Structure): Iterable[A] =
    existing.size match {

      case 0 =>
        Iterable.empty[A]

      case positiveSize =>
        val buffer = new ArrayBuffer[A](positiveSize)

          @tailrec def drain(current: Structure): Unit =
            takeMin(current) match {

              case Some((item, newPq)) =>
                buffer.append(item)
                drain(newPq)

              case None =>
                ()
            }

        drain(existing)
        buffer.toIterable
    }

  override def delete(item: A)(existing: Structure): Option[Structure] = {
    val (itemNotPresent, someChange) = delete_h(item, existing)
    if (someChange)
      Some(itemNotPresent)
    else
      None
  }

  import TreeParts._

  /**
   * ASSUMPTION
   *  -- Parameter deletedAny has default value false.
   */
  private def delete_h(item: A, existing: Structure, deletedAny: Boolean = false): (Structure, Boolean) =
    existing match {

      case Full(left, heapItem, right) =>

        val doDeletion = cmp.compare(item, heapItem) == Equivalent

        val (ltDeleted, ltAnyDeleted) = delete_h(item, left, deletedAny || doDeletion)
        val (rtDeleted, rtAnyDeleted) = delete_h(item, right, deletedAny || doDeletion)

        if (doDeletion)
          (mergeUnbounded(ltDeleted, rtDeleted), true)

        else
          (
            Full(
              left = ltDeleted,
              item = heapItem,
              right = rtDeleted
            ),
            ltAnyDeleted || rtAnyDeleted
          )

      case Empty =>
        (Empty, deletedAny)
    }

  private[ds] def mergeUnbounded(a: Structure, b: Structure): Structure =
    a match {

      case Empty =>
        b match {

          case Empty =>
            Empty

          case Full(_, _, _) =>
            b
        }

      case Full(aLeft, aItem, aRight) =>
        b match {

          case Empty =>
            a

          case Full(bLeft, bItem, bRight) =>
            cmp.compare(aItem, bItem) match {

              case Less =>
                // a is more minium than b, so "a" should become the new
                // head of this heap that we're currently merging together
                Full(
                  left = aLeft,
                  item = aItem,
                  right = mergeUnbounded(aRight, b)
                )

              case Greater =>
                // b is more minum than a, so "b" should become the new
                // head of this heap that we're currently merging together
                Full(
                  left = mergeUnbounded(a, bLeft),
                  item = bItem,
                  right = bRight
                )

              case Equivalent =>
                // a and b are equivalent in priority, chose how to merge
                // based upon whichever Structure is smaller in size
                // (This is to keep the heap as balanced as we can!)
                if (a.size < b.size)
                  Full(
                    left = aLeft,
                    item = aItem,
                    right = mergeUnbounded(aRight, b)
                  )
                else
                  Full(
                    left = mergeUnbounded(a, bLeft),
                    item = bItem,
                    right = bRight
                  )
            }
        }
    }
}

object MinHeap {

  object Bounded {

    def apply[A: Cmp](maximumHeapSize: Int): MinHeap[A]#Bounded = {

      val module = new MinHeapImplementation[A](Some(maximumHeapSize))

      new MinHeap[A] with BoundedContainer[A, TreeParts.Tree[A]] {

        override val maxSize = module.maxSize

        override def peekMin(existing: Structure): Option[A] =
          module.peekMin(existing)

        override def takeMin(a: Structure): Option[(A, Structure)] =
          module.takeMin(a)

        override def merge(a: Structure, b: Structure): (Structure, Option[Iterable[A]]) =
          module.merge(a, b)

        override def insert(item: A)(existing: Structure): (Structure, Option[A]) =
          module.insert(item)(existing)
      }
    }
  }

  object Unbounded {

    def apply[A: Cmp]: MinHeap[A]#Unbounded = {

      val module = new MinHeapImplementation[A](None)

      new MinHeap[A] with UnboundedContainer[A, TreeParts.Tree[A]] {

        override def peekMin(existing: Structure): Option[A] =
          module.peekMin(existing)

        override def takeMin(a: Structure): Option[(A, Structure)] =
          module.takeMin(a)

        override def merge(a: Structure, b: Structure): Structure =
          module.merge(a, b)._1

        override def insert(item: A)(existing: Structure): Structure =
          module.insert(item)(existing)._1
      }
    }
  }
}

private class MinHeapImplementation[A: Cmp](maximumHeapSize: Option[Int])
    extends MinHeap[A]
    with BoundedContainer[A, TreeParts.Tree[A]] {

  import TreeParts._

  // we unpack here to use it internally, if applicable
  val (isMaxSizeDefined, maxSize) = {
    val ms = maximumHeapSize.map { v => math.max(0, v) }
    (ms.isDefined, ms.getOrElse(-1))
  }

  override def takeMin(existing: Structure): Option[(A, Structure)] =
    existing match {

      case Empty =>
        None

      case Full(left, minimum, right) =>
        // Since we're removing, there's no chance that merge will evaluate
        // with any removed items (in the Option[Iterable[A]])
        Some((minimum, mergeUnbounded(left, right)))
    }

  override def insert(item: A)(existing: Structure): (Structure, Option[A]) =
    insert_h(item, existing, existing.size)

  private[ds] def insert_h(item: A, existing: Structure, currentSize: Int): (Structure, Option[A]) =
    existing match {

      case Empty =>
        if (!isMaxSizeDefined || currentSize < maxSize)
          (Full(Empty, item, Empty), None)
        else
          (existing, Some(item))

      case Full(left, heapItem, right) =>
        cmp.compare(item, heapItem) match {

          case Less =>
            // item is "more minimum" than heap item: push heap-item down
            val ((newLeft, newRight), kickedOut) = newLeftAndRight(heapItem, left, right, currentSize)
            (
              Full(
                left = newLeft,
                item = item,
                right = newRight
              ),
              kickedOut
            )

          case Greater | Equivalent =>
            // item is either "less minimum" or "the same priority" to the heap item:
            // continue down heap to find appropriate spot
            left match {

              case Empty =>
                right match {

                  case Empty =>
                    val (newLeft, kickedOut) = insert_h(item, Empty, currentSize)
                    (
                      Full(
                        left = newLeft,
                        item = heapItem,
                        right = Empty
                      ),
                      kickedOut
                    )

                  case Full(_, _, _) =>
                    val (newLeft, kickedOut) = insert_h(item, Empty, currentSize)
                    (
                      Full(
                        left = newLeft,
                        item = heapItem,
                        right
                      ),
                      kickedOut
                    )
                }

              case Full(_, _, _) =>

                right match {

                  case Empty =>
                    val (newRight, kickedOut) = insert_h(item, Empty, currentSize)
                    (
                      Full(
                        left = left,
                        item = heapItem,
                        right = newRight
                      ),
                      kickedOut
                    )

                  case Full(_, _, _) =>
                    val ((newLeft, newRight), kickedOut) = newLeftAndRight(item, left, right, currentSize)
                    (
                      Full(
                        left = newLeft,
                        item = heapItem,
                        right = newRight
                      ),
                      kickedOut
                    )
                }
            }
        }
    }

  private def newLeftAndRight(
    theItem: A,
    left:    Structure,
    right:   Structure,
    size:    Int
  ): ((Structure, Structure), Option[A]) = {
    left match {

      case Empty =>
        val (result, kickedOut) = insert_h(theItem, Empty, size)
        ((result, right), kickedOut)

      case Full(_, leftItem, _) =>
        right match {

          case Empty =>
            val (result, kickedOut) = insert_h(theItem, Empty, size)
            ((left, result), kickedOut)

          case Full(_, rightItem, _) =>
            cmp.compare(leftItem, rightItem) match {

              case Less =>
                val (result, kickedOut) = insert_h(theItem, right, size)
                ((left, result), kickedOut)

              case Greater =>
                val (result, kickedOut) = insert_h(theItem, left, size)
                ((result, right), kickedOut)

              case Equivalent =>
                if (left.size < right.size) {
                  val (result, kickedOut) = insert_h(theItem, left, size)
                  ((result, right), kickedOut)

                } else {
                  val (result, kickedOut) = insert_h(theItem, right, size)
                  ((left, result), kickedOut)
                }
            }
        }
    }

  }

  def merge(a: Structure, b: Structure): (Structure, Option[Iterable[A]]) =
    if (isMaxSizeDefined && a.size + b.size > maxSize)
      mergeBounded(a, b)
    else
      (mergeUnbounded(a, b), None)

  // assumes maxSize.isDefined !!!
  private def mergeBounded(a: Structure, b: Structure): (Structure, Option[Iterable[A]]) = {
    val (smaller, larger) =
      if (a.size < b.size)
        (a, b)
      else
        (b, a)

    BoundedContainer.insert(this)(larger, sort(smaller).toSeq: _*)
  }

}

/*
object MinHeap {

  def apply[A: Cmp](maximumHeapSize: Option[Int]): MinHeap[A] =
    new MinHeap[A] {

      override val maxSize = maximumHeapSize.map { v => math.max(0, v) }
      // we unpack here to use it internally, if applicable
      private val isMaxSizeDefined = maxSize.isDefined
      private val maxSizeIfDefined = maxSize.getOrElse(-1)

      private val cmp = implicitly[Cmp[A]]

      override def peekMin(existing: Structure): Option[A] =
        existing.map(identity[A])



      override def takeMin(existing: Structure): Option[(A, Structure)] =
        existing match {

          case Empty =>
            None

          case Full(left, minimum, right) =>
            // Since we're removing, there's no chance that merge will evaluate
            // with any removed items (in the Option[Iterable[A]])
            Some((minimum, mergeUnbounded(left, right)))
        }

    }

    override def insert(item: A)(existing: Structure): (Structure, Option[A]) =
        insert_h(item, existing, existing.size)

      private[use] def insert_h(item: A, existing: Structure, currentSize: Int): (Structure, Option[A]) =
        existing match {

          case Empty =>
            if (!isMaxSizeDefined || currentSize < maxSizeIfDefined)
              (Full(Empty, item, Empty), None)
            else
              (existing, Some(item))

          case Full(left, heapItem, right) =>
            cmp.compare(item, heapItem) match {

              case Less =>
                // item is "more minimum" than heap item: push heap-item down
                val ((newLeft, newRight), kickedOut) = newLeftAndRight(heapItem, left, right, currentSize)
                (
                  Full(
                    left = newLeft,
                    item = item,
                    right = newRight
                  ),
                  kickedOut
                )

              case Greater | Equivalent =>
                // item is either "less minimum" or "the same priority" to the heap item:
                // continue down heap to find appropriate spot
                left match {

                  case Empty =>
                    right match {

                      case Empty =>
                        val (newLeft, kickedOut) = insert_h(item, Empty, currentSize)
                        (
                          Full(
                            left = newLeft,
                            item = heapItem,
                            right = Empty
                          ),
                          kickedOut
                        )

                      case Full(_, _, _) =>
                        val (newLeft, kickedOut) = insert_h(item, Empty, currentSize)
                        (
                          Full(
                            left = newLeft,
                            item = heapItem,
                            right
                          ),
                          kickedOut
                        )
                    }

                  case Full(_, _, _) =>

                    right match {

                      case Empty =>
                        val (newRight, kickedOut) = insert_h(item, Empty, currentSize)
                        (
                          Full(
                            left = left,
                            item = heapItem,
                            right = newRight
                          ),
                          kickedOut
                        )

                      case Full(_, _, _) =>
                        val ((newLeft, newRight), kickedOut) = newLeftAndRight(item, left, right, currentSize)
                        (
                          Full(
                            left = newLeft,
                            item = heapItem,
                            right = newRight
                          ),
                          kickedOut
                        )
                    }
                }
            }
        }

      private def newLeftAndRight(
        theItem: A,
        left:    Structure,
        right:   Structure,
        size:    Int
      ): ((Structure, Structure), Option[A]) = {
        left match {

          case Empty =>
            val (result, kickedOut) = insert_h(theItem, Empty, size)
            ((result, right), kickedOut)

          case Full(_, leftItem, _) =>
            right match {

              case Empty =>
                val (result, kickedOut) = insert_h(theItem, Empty, size)
                ((left, result), kickedOut)

              case Full(_, rightItem, _) =>
                cmp.compare(leftItem, rightItem) match {

                  case Less =>
                    val (result, kickedOut) = insert_h(theItem, right, size)
                    ((left, result), kickedOut)

                  case Greater =>
                    val (result, kickedOut) = insert_h(theItem, left, size)
                    ((result, right), kickedOut)

                  case Equivalent =>
                    if (left.size < right.size) {
                      val (result, kickedOut) = insert_h(theItem, left, size)
                      ((result, right), kickedOut)

                    } else {
                      val (result, kickedOut) = insert_h(theItem, right, size)
                      ((left, result), kickedOut)
                    }
                }
            }
        }

      }

      override def merge(a: Structure, b: Structure): (Structure, Option[Iterable[A]]) =
        if (maxSize.isDefined && a.size + b.size > maxSizeIfDefined)
          mergeBounded(a, b)
        else
          (mergeUnbounded(a, b), None)

      // assumes maxSize.isDefined !!!
      private def mergeBounded(a: Structure, b: Structure): (Structure, Option[Iterable[A]]) = {
        val (smaller, larger) =
          if (a.size < b.size)
            (a, b)
          else
            (b, a)

        SortableContainer.insert(this)(larger, sort(smaller))
      }

      private def mergeUnbounded(a: Structure, b: Structure): Structure =
        a match {

          case Empty =>
            b match {

              case Empty =>
                Empty

              case Full(_, _, _) =>
                b
            }

          case Full(aLeft, aItem, aRight) =>
            b match {

              case Empty =>
                a

              case Full(bLeft, bItem, bRight) =>
                cmp.compare(aItem, bItem) match {

                  case Less =>
                    // a is more minium than b, so "a" should become the new
                    // head of this heap that we're currently merging together
                    Full(
                      left = aLeft,
                      item = aItem,
                      right = mergeUnbounded(aRight, b)
                    )

                  case Greater =>
                    // b is more minum than a, so "b" should become the new
                    // head of this heap that we're currently merging together
                    Full(
                      left = mergeUnbounded(a, bLeft),
                      item = bItem,
                      right = bRight
                    )

                  case Equivalent =>
                    // a and b are equivalent in priority, chose how to merge
                    // based upon whichever Structure is smaller in size
                    // (This is to keep the heap as balanced as we can!)
                    if (a.size < b.size)
                      Full(
                        left = aLeft,
                        item = aItem,
                        right = mergeUnbounded(aRight, b)
                      )
                    else
                      Full(
                        left = mergeUnbounded(a, bLeft),
                        item = bItem,
                        right = bRight
                      )
                }
            }
        }

}
*/ 