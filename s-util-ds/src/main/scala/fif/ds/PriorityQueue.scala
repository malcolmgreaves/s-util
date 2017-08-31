package fif.ds

import algebra.Eq

/**
  * Removes duplicates according to Eq[A]. Orders on priority according to Cmp[A]
  */
abstract class PriorityQueue[A: Cmp: Eq, S]
    extends SortableContainer[A, S]
    with FastMin[A, S]
    with SearchableContainer[A, S] {

  type Bounded =
    PriorityQueue[A, this.Structure] with BoundedContainer[A, this.Structure]
  type Unbounded =
    PriorityQueue[A, this.Structure] with UnboundedContainer[A, this.Structure]
}
