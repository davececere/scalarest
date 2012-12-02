organization := "com.example"

name := "scalarest"

version := "0.1.0-SNAPSHOT"

resolvers += "releases"  at "http://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
   "net.databinder" %% "unfiltered-filter" % "0.6.4",
   "javax.servlet" % "servlet-api" % "2.3" % "provided",
   "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "container",
   "org.clapper" %% "avsl" % "0.4",
   "net.databinder" %% "unfiltered-spec" % "0.6.4" % "test"
)

seq(webSettings :_*)
