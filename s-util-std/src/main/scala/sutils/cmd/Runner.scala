package sutils.cmd

import scala.util.Try
import sutils.std.ImplicitTryOps

abstract class Runner {

  import ImplicitTryOps._
  import RunnerHelpers._

  /** Specific type for arguments: parsed and run by main. */
  type Arguments

  /** Parses command line arguments into an Arguments instance. */
  final type ArgParser = Seq[String] => Arguments

  /** The main, executable functionality: runs with an Arguments instance as input. */
  final type Main = Arguments => Unit

  /** Help message to display. */
  val helpMsg: String

  /** Function to process command-line arguments into an Arguments instance. */
  val parseArgsUnsafe: ArgParser

  /** Actual main, running functionality: wrapped by error-handling code. */
  val main_hUnsafe: Main

  /** Parses args, runs the main function, and handles errors. */
  final def main(cmdLineArgs: Array[String]): Unit =
    if(shouldPrintHelp(cmdLineArgs)) {
      log(helpMsg)
      goodExit()

    } else
      Try {
        parseArgsUnsafe {
          Option { cmdLineArgs }
            .map { _.toSeq }
            .getOrElse { Seq.empty[String] }
        }
      }.effectOnFailure { e =>
        log(s"Error parsing arguments (# ${cmdLineArgs.length}):\n\n$e\n\n")
        e.printStackTrace(System.err)
        log(s"Actual command line arguments:\n\n${cmdLineArgs.toSeq}\n\n")
        log(s"Help message:\n\n$helpMsg")
        badExit()
      }
        .flatMap { args => Try { main_hUnsafe { args } } }
        .fold(
          e => {
            log(s"$time FAILURE of [ $name ]\n\n$e\n\n")
            e.printStackTrace(System.err)
            badExit()
          },
          _ => {
            log(s"$time done! safe to exit [ $name ]")
            goodExit()
          }
        )

  /** The runner's name. Defaults to the class name. */
  lazy val name: String =
    this.getClass.getName

}
