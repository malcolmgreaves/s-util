package fif.ds

trait Container[A, S] extends Serializable {

  type Structure = S

  def empty: Structure

}
