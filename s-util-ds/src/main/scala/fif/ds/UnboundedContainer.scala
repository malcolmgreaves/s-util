package fif.ds

import scala.language.postfixOps

trait UnboundedContainer[A, S] extends Container[A, S] {

  def merge(a: Structure, b: Structure): Structure

  def insert(item: A)(existing: Structure): Structure

}

object UnboundedContainer {

  def merge[A, S](module: UnboundedContainer[A, S])(
    initial:    module.Structure,
    structures: module.Structure*
  ): module.Structure =
    structures.foldLeft(initial) {
      case (merging, next) =>
        module.merge(merging, next)
    }

  def insert[A, S](module: UnboundedContainer[A, S])(
    existing: module.Structure,
    elements: A*
  ): module.Structure =
    elements.foldLeft(existing) {
      case (pq, aItem) =>
        module.insert(aItem)(pq)
    }

  def insert[A, S](
    module:   UnboundedContainer[A, S],
    elements: A*
  ): module.Structure =
    insert(module)(module.empty, elements: _*)

}

