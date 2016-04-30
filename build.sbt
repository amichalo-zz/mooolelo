name := "mooolelo"
version := "0.1"
organization := "com.amichalo"
scalaVersion := "2.11.7"

scalacOptions := Seq("-target:jvm-1.8", "-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.14"
  val akkaStreamV = "1.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "joda-time" % "joda-time" % "2.4",
    "org.reactivemongo" %% "reactivemongo" % "0.11.10",
    "ch.qos.logback" % "logback-core" % "1.1.2",
    "ch.qos.logback" % "logback-classic" % "1.1.2"
  )
}

Revolver.settings