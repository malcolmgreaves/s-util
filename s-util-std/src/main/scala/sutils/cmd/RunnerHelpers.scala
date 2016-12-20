package sutils.cmd

import java.util.Date

object RunnerHelpers {

  def shouldPrintHelp(args: Array[String]): Boolean =
    args.size == 1 && (args.head == "-h" || args.head == "--help")

  /** Writes the input String to stderr. */
  def log(s: => String): Unit =
    System.err.println(s)

  /** Creates a new Java Date instance and calls its toString. */
  def time: String =
    s"[${new Date().toString}]"

  /** Ends process with error code 0. */
  def goodExit(): Unit =
    System.exit(0)

  /** Ends process with error code 1. */
  def badExit(): Unit =
    System.exit(1)

}
