package fif.ds

import scala.annotation.tailrec

case class BinarySearchTree[A: Cmp]()
    extends SortableContainer[A, TreeParts.Tree[A]]
    with TreeLikeContainer[A]
    with SearchableContainer[A, TreeParts.Tree[A]]
    with UnboundedContainer[A, TreeParts.Tree[A]] {

  override val cmp = implicitly[Cmp[A]]

  import TreeParts._

  override def sort(existing: Structure): Iterable[A] =
    existing match {

      case Empty =>
        Iterable.empty[A]

      case Full(left, item, right) =>
        sort(left) ++ Iterable(item) ++ sort(right)
    }

  override def contains(item: A)(existing: Structure): Boolean =
    contains_h(item, existing)

  @tailrec private def contains_h(item: A, existing: Structure): Boolean =
    existing match {

      case Empty =>
        false

      case Full(left, treeItem, right) =>
        cmp.compare(item, treeItem) match {

          case Equivalent =>
            true

          case Less =>
            contains_h(item, left)

          case Greater =>
            contains_h(item, right)
        }
    }

  override def delete(item: A)(existing: Structure): Option[Structure] =
    existing match {

      case Empty =>
        None

      case f @ Full(left, treeItem, right) =>
        cmp.compare(item, treeItem) match {

          case Equivalent =>
            Some(merge(left, right))

          case Less =>
            delete(item)(left).map { newLeft =>
              f.copy(left = newLeft)
            }

          case Greater =>
            delete(item)(right).map { newRight =>
              f.copy(right = newRight)
            }
        }
    }

  override def merge(a: Structure, b: Structure): Structure = {
    val (smaller, larger) =
      if (a.size < b.size)
        (a, b)
      else
        (b, a)

    sort(smaller).foldLeft(larger) {
      case (merging, item) =>
        insert(item)(merging)
    }
  }

  override def insert(item: A)(existing: Structure): Structure =
    existing match {

      case Empty =>
        Full(Empty, item, Empty)

      case f @ Full(left, treeItem, right) =>
        cmp.compare(item, treeItem) match {

          case Less =>
            f.copy(
              left = insert(item)(left)
            )

          case Greater =>
            f.copy(
              right = insert(item)(right)
            )

          case Equivalent =>
            // We have the option to insert into either subtree.
            // In an attempt to keep things balanced,
            // we insert into the smaller subtree.
            if (left.size < right.size)
              f.copy(
                left = insert(item)(left)
              )
            else
              f.copy(
                right = insert(item)(right)
              )
        }
    }

}
