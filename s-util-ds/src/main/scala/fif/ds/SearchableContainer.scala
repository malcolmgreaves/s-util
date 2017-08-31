package fif.ds

import scala.language.postfixOps

trait SearchableContainer[A, S] extends Container[A, S] {

  def contains(item: A)(existing: Structure): Boolean

}