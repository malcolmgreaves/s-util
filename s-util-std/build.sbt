name := "s-util-std"

import sbt._
import Keys._
import SharedBuild._

//
// NO compile libraryDependencies !!!
//

libraryDependencies ++= testDeps

//
// test, runtime settings
//
fork in run := true
fork in Test := true
parallelExecution in Test := true
