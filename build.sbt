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
  "-Xfatal-warnings", // Every warning is esclated to an error.
  "-opt:l:inline",
  "-opt-inline-from:**",
  "-deprecation",
  "-feature",
  "-unchecked",
  s"-target:jvm-$javaV",
  "-encoding",
  "utf8",
  "-language:postfixOps",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-language:reflectiveCalls",
  "-Yno-adapted-args",
  "-Ywarn-value-discard",
  "-Xlint",
  "-Xfuture",
  "-Ywarn-dead-code"
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
