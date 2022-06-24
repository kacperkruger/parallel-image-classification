ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ayaod"
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations"
)

val circeVersion = "0.14.1"
val akkaVersion = "2.6.14"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"
).map(_ % akkaVersion)
