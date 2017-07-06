name := "scala_hello_world"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4-SNAPSHOT"
libraryDependencies += "com.ning" % "async-http-client" % "1.9.40"
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "commons-validator" % "commons-validator" % "1.5+"
libraryDependencies += "com.outworkers" % "phantom-dsl_2.11" % "2.12.1"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"