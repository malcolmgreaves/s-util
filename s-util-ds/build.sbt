name := "s-util-ds"

import sbt._
import Keys._
import SharedBuild._

addCompilerPlugin(scalaMacros)

libraryDependencies ++= testDeps

//
// test, runtime settings
//
fork in run               := true
fork in Test              := true
parallelExecution in Test := true
