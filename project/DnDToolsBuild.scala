import sbt._
import sbt.Keys._

object DnDToolsBuild extends Build {

  lazy val dndtools = Project(
    id = "dndtools",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "Dnd Tools",
      organization := "com.larskroll",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      scalacOptions += "-Ydependent-method-types",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      resolvers += "spray repo" at "http://repo.spray.io",
      resolvers += "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/",
      resolvers += "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.3",
      libraryDependencies += "io.spray" % "spray-can" % "1.0-M4.2",
      libraryDependencies += "io.spray" % "spray-routing" % "1.0-M4.2",
      libraryDependencies += "io.spray" % "spray-caching" % "1.0-M4.2",
      libraryDependencies += "play" %% "anorm" % "2.1-0620-sbt12",
      libraryDependencies += "com.github.seratch" %% "scalikejdbc" % "[1.3,)",
      libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.21",
      libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.6",
      libraryDependencies += "io.spray" %%  "spray-json" % "1.2.2" cross CrossVersion.full
    )
  )
}
