name := "bux-assignment"

version := "0.1"

scalaVersion := "2.13.6"

val http4sVersion = "0.23.4"
val circeVersion  = "0.14.1"

libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-blaze-client"    % http4sVersion
  , "org.http4s" %% "http4s-circe"           % http4sVersion
  , "org.http4s" %% "http4s-jdk-http-client" % "0.5.0"
  , "org.slf4j"   % "slf4j-simple"           % "1.7.32"
  , "io.circe"   %% "circe-core"             % circeVersion
  , "io.circe"   %% "circe-generic"          % circeVersion
  , "io.circe"   %% "circe-parser"           % circeVersion
)
