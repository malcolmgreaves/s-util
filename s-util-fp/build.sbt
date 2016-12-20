name := "s-util-fp"

import sbt._
import Keys._
import SharedBuild._

addCompilerPlugin(scalaMacros)

libraryDependencies ++= fpDeps
libraryDependencies ++= testDeps

//
// test, runtime settings
//
fork in run := true
fork in Test := true
parallelExecution in Test := true
