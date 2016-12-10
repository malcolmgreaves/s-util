package sutils.std

object StringHelpers {

  def split(
    clean: String => String,
    pass: String => Boolean,
    trimRawInput: Boolean = true)(
      regexSeperator: String)(
        rawInput: String): Seq[String] =
    {
      if (trimRawInput)
        rawInput.trim()
      else
        rawInput
    }
      .split(regexSeperator)
      .map(clean)
      .filter(pass)
      .toSeq

  def split(regexSeperator: String)(rawInput: String): Seq[String] =
    split(
      identity,
      _ => true,
      trimRawInput = true
    )(regexSeperator)(rawInput)

  def lcaseSpaceSplit(rawInput: String): Seq[String] =
    split(
      _.toLowerCase,
      _ => true,
      trimRawInput = true
    )(" ")(rawInput)

  type Combiner = Seq[String] => String

  def combine(rawSeperator: String): Combiner =
    _.mkString(rawSeperator)

  lazy val tabCombine: Combiner = combine("\t")
  lazy val spaceCombine: Combiner = combine("\t")
  lazy val newlineCombine: Combiner = combine("\n")

  @inline def insert(
    bits: Seq[String],
    index: Int,
    x: String): Seq[String] =

    if (index <= 0)
      x +: bits

    else if (index >= bits.size)
      bits :+ x

    else
      (bits.slice(0, index) :+ x) ++ bits.slice(index, bits.size)

  @inline def prepend(bits: Seq[String], x: String): Seq[String] =
    insert(bits, 0, x)

  @inline def append(bits: Seq[String], x: String): Seq[String] =
    insert(bits, bits.size, x)

}