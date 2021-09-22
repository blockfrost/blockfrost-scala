scalaVersion := "2.13.6"
organization := "io.blockfrost"
name := "blockfrost-scala"
version := "0.1.0"

val json4sVersion = "4.0.3"
val sttpVersion = "3.3.13"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client3" %% "json4s" % sttpVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",

  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttpVersion % Test,
  "com.avsystem.commons" %% "commons-core" % "2.2.6" % Test
)
