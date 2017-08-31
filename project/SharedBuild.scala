import sbt._
import Keys._

object SharedBuild {

  // // // // // // // //
  // //   Versions  // //
  // // // // // // // //

  lazy val scalazV = "7.2.15"
  lazy val macroV = "2.1.0"
  lazy val algebraV = "0.4.2"

  // // // // // // // // // //
  // //    Dependencies   // //
  // // // // // // // // // //

  lazy val scalaMacros =
    "org.scalamacros" % "paradise" % macroV cross CrossVersion.full

  lazy val fpDeps = Seq(
    "org.scalaz" %% "scalaz-core" % scalazV
  )

  lazy val dsDeps = Seq(
    "org.spire-math" %% "algebra" % algebraV
  )

  lazy val testDeps = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )

  // // // // // // // // // //
  // //     Publishing    // //
  // // // // // // // // // //

  case class RepoInfo(
      group: String,
      name: String
  )

  lazy val doPublish = (ri: RepoInfo) =>
    Seq(
      publishMavenStyle := true,
      isSnapshot := false,
      publishArtifact in Test := false,
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
      },
      pomIncludeRepository := { _ =>
        false
      },
      pomExtra := {
        <url>https://github.com/{ ri.group }/{ ri.name }</url>
        <licenses>
          <license>
            <name>Apache 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>Yes</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:{ ri.group }/{ ri.name }.git</url>
          <connection>scm:git@github.com:{ ri.group }/{ ri.name }.git</connection>
        </scm>
        <developers>
          <developer>
            <id>malcolmgreaves</id>
            <name>Malcolm W. Greaves</name>
            <email>greaves.malcolm@gmail.com</email>
            <url>https://malcolmgreaves.io/</url>
          </developer>
        </developers>
      },
      publishArtifact := true
  )

  lazy val noPublish = Seq(
    isSnapshot := true,
    publishArtifact in Test := false,
    publishTo := None,
    pomIncludeRepository := { _ =>
      false
    },
    pomExtra := { <nothing></nothing> },
    publishLocal := {},
    publish := {},
    publishArtifact := false
  )

}
