package sutils.std

import scala.util.Try
import java.io.{ File, FileWriter, BufferedWriter }

object IoHelpers {

  def outputToFile(base: File, specific: String): Seq[String] => Try[Unit] =
    outputToFile(new File(base, specific))

  def outputToFile(fi: File): Seq[String] => Try[Unit] =
    lines => Try {
      val w = new BufferedWriter(new FileWriter(fi))
      try {
        lines foreach { line =>
          w.write(line)
          w.newLine()
        }
      } finally { w.close() }
    }

}