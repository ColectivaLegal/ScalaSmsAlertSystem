name := "SmsAlertSystemV2"
 
version := "1.0" 
      
lazy val `smsalertsystemv2` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ehcache,
  ws,
  specs2 % Test,
  guice,
  "com.twilio.sdk" % "twilio" % "7.15.0",
  "com.typesafe.play" %% "play-slick" %  "3.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.2",
  "com.h2database" % "h2" % "1.4.194",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)      