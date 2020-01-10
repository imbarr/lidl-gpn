import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val dependencies = Seq(
  scalaTest % Test,

  "org.apache.kafka" % "kafka-streams" % "2.4.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.4.0",
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.1.0",

  "org.typelevel" %% "cats-effect" % "2.0.0",
  "net.codingwell" %% "scala-guice" % "4.2.6",
)

lazy val kafkaCommons = ProjectRef(file("../commons"), id = "commons")

lazy val root = Project(id = "journal-service", base = file("."))
  .dependsOn(kafkaCommons)
  .settings(libraryDependencies ++= dependencies)
