package fif.ds

trait SearchableContainer[A, S] extends Container[A, S] {

  def contains(item: A)(existing: Structure): Boolean

}
