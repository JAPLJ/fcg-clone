name := """fcg-clone"""
organization := "com.japlj"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.0"

lazy val libraries =
  Seq(guice, "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test)

import play.sbt.PlayImport.PlayKeys.playRunHooks
import scala.sys.process.Process

val webpackCommand = "node_modules/.bin/webpack"
lazy val webpack = taskKey[Unit]("Run webpack when packaging the application")
def runWebpack(file: File): Int = {
  Process(webpackCommand, file, "BUILD_ENV" -> "production").run().exitValue()
}

webpack := {
  if (runWebpack(baseDirectory.value) != 0) {
    throw new Exception()
  }
}

dist := (dist dependsOn webpack).value
stage := (stage dependsOn webpack).value

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    updateOptions := updateOptions.value.withCachedResolution(true),
    scalaVersion := "2.13.0",
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false,
    libraryDependencies ++= libraries
  )
  .settings(
    playRunHooks += RunSubProcess(
      s"$webpackCommand --progress --colors --watch --display-error-details")
  )
  .enablePlugins(SbtWeb)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "helpers"
  )
