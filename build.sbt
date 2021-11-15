import org.jetbrains.sbtidea.Keys._

lazy val inlineError =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.1.0-dev",
      scalaVersion := "2.13.2",
      ThisBuild / intellijPluginName := "InlineError",
      ThisBuild / intellijBuild      := "212.5457.46",
      ThisBuild / intellijPlatform   := IntelliJPlatform.IdeaCommunity,
      Global    / intellijAttachSources := true,
      Compile   / javacOptions       ++= "--release" :: "11" :: Nil,
      Compile / unmanagedResourceDirectories += baseDirectory.value / "resource",
      Test / unmanagedResourceDirectories += baseDirectory.value / "testResources",
      signPluginOptions := signPluginOptions.value.copy(
        enabled=true
      )
    )
