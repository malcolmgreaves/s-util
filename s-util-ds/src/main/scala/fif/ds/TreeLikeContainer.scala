package fif.ds

trait TreeLikeContainer[A] extends Container[A, TreeParts.Tree[A]] {

  override val empty: Structure = TreeParts.Empty

}

object TreeParts {

  sealed abstract class Tree[+A] extends Serializable {
    val size: Int
    def map[B](f: A => B): Option[B]
  }

  case object Empty extends Tree[Nothing] {
    override val size = 0
    override def map[B](f: Nothing => B): Option[B] = None
  }

  case class Full[+A](left: Tree[A], item: A, right: Tree[A]) extends Tree[A] {
    override val size = left.size + 1 + right.size
    override def map[B](f: A => B): Option[B] = Some(f(item))
  }

}
