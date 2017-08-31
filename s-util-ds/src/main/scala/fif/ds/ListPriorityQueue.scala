package fif.ds

import algebra.Eq

import scala.annotation.tailrec

object ListPriorityQueue {

  object Bounded {

    def apply[A: Cmp: Eq](
        maximumHeapSize: Int): PriorityQueue[A, List[A]]#Bounded =
      new PriorityQueue[A, List[A]] with BoundedContainer[A, List[A]] {

        val module = new ListPriorityQueue[A](Some(maximumHeapSize))

        override def insert(item: A)(
            existing: Structure): (Structure, Option[A]) =
          module.insert(item)(existing)

        override def merge(one: Structure,
                           two: Structure): (Structure, Option[Iterable[A]]) =
          module.merge(one, two)

        override def delete(item: A)(existing: Structure): Option[Structure] =
          module.delete(item)(existing)

        override def sort(existing: Structure): Iterable[A] =
          module.sort(existing)

        override def contains(item: A)(existing: Structure): Boolean =
          module.contains(item)(existing)

        override def peekMin(existing: Structure): Option[A] =
          module.peekMin(existing)

        override def takeMin(existing: List[A]): Option[(A, Structure)] =
          module.takeMin(existing)

        override val empty = module.empty

        override val maxSize = module.maxSize

        override val cmp = module.cmp
      }
  }

  object Unbounded {

    def apply[A: Cmp: Eq]: PriorityQueue[A, List[A]]#Unbounded =
      new PriorityQueue[A, List[A]] with UnboundedContainer[A, List[A]] {

        val module = new ListPriorityQueue[A](None)

        override def insert(item: A)(existing: Structure): Structure =
          module.insert(item)(existing)._1

        override def merge(one: Structure, two: Structure): Structure =
          module.merge(one, two)._1

        override def delete(item: A)(existing: Structure): Option[Structure] =
          module.delete(item)(existing)

        override def sort(existing: Structure): Iterable[A] =
          module.sort(existing)

        override def contains(item: A)(existing: Structure): Boolean =
          module.contains(item)(existing)

        override def peekMin(existing: Structure): Option[A] =
          module.peekMin(existing)

        override def takeMin(existing: List[A]): Option[(A, Structure)] =
          module.takeMin(existing)

        override val empty = module.empty

        override val cmp = module.cmp
      }
  }
}

private class ListPriorityQueue[A: Cmp: Eq](maximumHeapSize: Option[Int])
    extends PriorityQueue[A, List[A]]
    with BoundedContainer[A, List[A]] {

  override type Structure = List[A]

  val (isMaxSizeDefined, maxSize) = {
    val ms = maximumHeapSize.map { v =>
      math.max(0, v)
    }
    (ms.isDefined, ms.getOrElse(-1))
  }

  override val empty: Structure =
    List.empty[A]

  override def peekMin(existing: Structure): Option[A] =
    existing.headOption

  override val cmp = implicitly[Cmp[A]]

  private val sortFn =
    (a: A, b: A) => cmp.compare(a, b) == Less

  private val equality =
    implicitly[Eq[A]].eqv _

  /**
    * Assumptions
    *  -- Input structure (existing) is in sorted order, ascending.
    *  -- This is the assumption for all instances of ListPriorityQueue[A]#Structure
    */
  override def contains(item: A)(existing: Structure): Boolean =
    binarySearch(item, existing)

  @tailrec private def binarySearch(item: A, existing: Structure): Boolean =
    if (existing isEmpty)
      false
    else {

      val middleIndex = existing.size / 2
      val middleItem = existing(middleIndex)

      if (equality(item, middleItem))
        true
      else
        binarySearch(
          item,
          cmp.compare(item, middleItem) match {

            case Less =>
              existing.slice(0, middleIndex)

            case Greater | Equivalent =>
              existing.slice(middleIndex + 1, existing.size)
          }
        )
    }

  override def insert(item: A)(existing: Structure): (Structure, Option[A]) =
    if (contains(item)(existing))
      (existing, None)
    else {
      val newList = (existing :+ item).sortWith(sortFn)
      if (isMaxSizeDefined && newList.size > maxSize) {
        val end = math.min(newList.size, maxSize)
        (newList.slice(0, end), Some(newList(end)))

      } else
        (newList, None)
    }

  override def merge(one: Structure,
                     two: Structure): (Structure, Option[Iterable[A]]) =
    if (one.isEmpty && two.isEmpty)
      (empty, None)
    else {
      val (smaller, larger) =
        if (one.size > two.size) (one, two)
        else (two, one)

      BoundedContainer.insert(this)(larger, smaller: _*)
    }

  override def takeMin(existing: List[A]): Option[(A, Structure)] =
    existing.headOption.map { head =>
      (head, existing.slice(1, existing.size))
    }

  override def sort(existing: Structure): Iterable[A] =
    existing.sortWith(sortFn)

  override def delete(item: A)(existing: Structure): Option[Structure] = {
    val (itemNotPresent, someChange) = delete_h(item, existing)
    if (someChange)
      Some(itemNotPresent)
    else
      None
  }

  /**
    * ASSUMPTION
    *  -- Parameter deletedAny has default value false.
    */
  private def delete_h(item: A,
                       existing: Structure,
                       deletedAny: Boolean = false): (Structure, Boolean) =
    existing match {

      case first :: rest =>
        if (equality(item, first)) {
          (delete_h(item, rest)._1, true)

        } else {
          val (continue, changed) = delete_h(item, rest, deletedAny)
          (continue, changed || deletedAny)
        }

      case Nil =>
        (Nil, deletedAny)
    }

}
