import org.jetbrains.sbtidea.Keys._

lazy val inlineError =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.0.1-SNAPSHOT",
      scalaVersion := "2.13.2",
      ThisBuild / intellijPluginName := "InlineError",
      ThisBuild / intellijBuild      := "211.7628.21",
      ThisBuild / intellijPlatform   := IntelliJPlatform.IdeaCommunity,
      Global    / intellijAttachSources := true,
      //Compile   / javacOptions       ++= "--release" :: "11" :: Nil,
      unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
      unmanagedResourceDirectories in Test    += baseDirectory.value / "testResources",
    )
