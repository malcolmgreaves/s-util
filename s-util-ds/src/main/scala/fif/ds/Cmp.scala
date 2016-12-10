package fif.ds

import scala.language.higherKinds

trait Cmp[A] extends Serializable {
  def compare(a: A, b: A): Comparision
}

sealed trait Comparision extends Serializable
case object Less extends Comparision
case object Greater extends Comparision
case object Equivalent extends Comparision

object Cmp extends Serializable {

  def numeric[N: Numeric]: Cmp[N] =
    new Cmp[N] {
      override def compare(a: N, b: N): Comparision = {
        val c = implicitly[Numeric[N]].compare(a, b)
        if (c < 0)
          Less
        else if (c > 0)
          Greater
        else
          Equivalent
      }
    }

  object Implicits extends Serializable {

    implicit val intCmp: Cmp[Int] =
      numeric[Int]

    implicit val strCmp: Cmp[String] =
      new Cmp[String] {
        override def compare(a: String, b: String): Comparision = {
          val c = a.compare(b)
          if (c < 0)
            Less
          else if (c > 0)
            Greater
          else
            Equivalent
        }
      }

  }
}