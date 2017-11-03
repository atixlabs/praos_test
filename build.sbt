enablePlugins(JavaAppPackaging)

val commonSettings = Seq(
  name := "praos_test",
  version := "0.1",
  scalaVersion := "2.12.1"
)

val dep = {
  val akkaVersion = "2.4.17"

  Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.9",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test",
    "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
  )
}

val root = project.in(file("."))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= dep)

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Xlint:unsound-match",
  "-Ywarn-inaccessible",
  "-Ywarn-unused-import"
)

parallelExecution in Test := false

testOptions in Test += Tests.Argument("-oD")

mainClass in Compile := Some("io.iohk.praos.App")

scalacOptions in (Compile, console) ~= (_.filterNot(Set(
  "-Ywarn-unused-import",
  "-Xfatal-warnings"
)))