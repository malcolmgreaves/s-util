name := "s-util"

import SharedBuild.{doPublish, noPublish, RepoInfo}

scalaVersion in ThisBuild := "2.12.3"
crossScalaVersions := Seq("2.11.8", scalaVersion.value)
organization in ThisBuild := "io.malcolmgreaves"
version in ThisBuild := {
  val major: Int = 0
  val minor: Int = 5
  val patch: Int = 0
  s"$major.$minor.$patch"
}

lazy val root = project
  .in(file("."))
  .aggregate(
    `s-util-std`,
    `s-util-fp`,
    `s-util-ds`
  )
  .settings { noPublish }

lazy val `s-util-std` = project.in(file("s-util-std")).settings {
  doPublish {
    RepoInfo(group = "malcolmgreaves", name = "s-util-std")
  }
}

lazy val `s-util-fp` =
  project.in(file("s-util-fp")).dependsOn(`s-util-std`).settings {
    doPublish {
      RepoInfo(group = "malcolmgreaves", name = "s-util-fp")
    }
  }

lazy val `s-util-ds` =
  project.in(file("s-util-ds")).dependsOn(`s-util-fp`).settings {
    doPublish {
      RepoInfo(group = "malcolmgreaves", name = "s-util-ds")
    }
  }

lazy val subprojects: Seq[ProjectReference] = root.aggregate
lazy val publishTasks = subprojects.map { publish.in }

resolvers in ThisBuild := Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Scalaz Bintray" at "http://dl.bintray.com/scalaz/releases"
)

lazy val javaV = "1.8"
scalacOptions in ThisBuild := Seq(
  "-opt:l:inline",
  "-opt-inline-from:**",
  s"-target:jvm-$javaV",
//  "-Xsource:2.11", // TODO: remove !!!
  "-encoding",
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)
javacOptions in ThisBuild := Seq("-source", javaV, "-target", javaV)
javaOptions in ThisBuild := Seq(
  "-XX:+UseG1GC",
  "-server",
  "-XX:+AggressiveOpts",
  "-XX:+TieredCompilation",
  "-XX:CompileThreshold=420",
  "-Xmx3000M"
)

scalacOptions in (Compile, console) ~= (_.filterNot(
  Set(
    "-Ywarn-unused:imports",
    "-Xfatal-warnings"
  )))
