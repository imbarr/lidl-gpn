import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val dependencies = Seq(
  scalaTest % Test,

  "org.apache.kafka" % "kafka-streams" % "2.4.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.4.0",

  "io.circe" %% "circe-core" % "0.12.3",
  "io.circe" %% "circe-generic" % "0.12.3",
  "io.circe" %% "circe-parser" % "0.12.3",

  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",

  "org.typelevel" %% "cats-effect" % "2.0.0",
)

lazy val root = Project(id = "commons", base = file("."))
  .settings(libraryDependencies ++= dependencies)
