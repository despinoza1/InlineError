import org.jetbrains.sbtidea.Keys._

lazy val inlineError =
  project.in(file("."))
    .enablePlugins(SbtIdeaPlugin)
    .settings(
      version := "0.4.0",
      scalaVersion := "2.13.8",
      ThisBuild / intellijPluginName := "InlineError",
      ThisBuild / intellijBuild      := "213.6461.79",
      ThisBuild / intellijPlatform   := IntelliJPlatform.IdeaCommunity,
      Global    / intellijAttachSources := true,
      Compile   / javacOptions       ++= "--release" :: "11" :: Nil,
      Compile / unmanagedResourceDirectories += baseDirectory.value / "resource",
      Test / unmanagedResourceDirectories += baseDirectory.value / "testResources",
      patchPluginXml := pluginXmlOptions { xml =>
        xml.version = version.value
      },
      signPluginOptions := signPluginOptions.value.copy(
        enabled=true
      ),
      publishPlugin := (if (version.value.endsWith("dev")) "Dev" else "Stable")
    )
