package fif.ds

import scala.language.postfixOps

trait SortableContainer[A, S] extends Container[A, S] {

  val cmp: Cmp[A]

  def sort(existing: Structure): Iterable[A]

  def delete(item: A)(existing: Structure): Option[Structure]

}

object SortableContainer {

  def delete[A, S](module: SortableContainer[A, S])(
    existing: module.Structure,
    elements: A*): Option[module.Structure] = {

    val (elementsNotInThisStructure, removedAny) =
      elements.foldLeft((existing, false)) {
        case ((removing, check), item) =>
          module.delete(item)(removing) match {

            case Some(removedFrom) =>
              (removedFrom, true)

            case None =>
              (removing, check)

          }
      }

    if (removedAny)
      Some(elementsNotInThisStructure)
    else
      None
  }

}