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
      patchPluginXml := pluginXmlOptions { xml =>
        xml.version = version.value
        xml.sinceBuild = (ThisBuild / intellijBuild).value
        xml.changeNotes =
          """ <![CDATA[
            |   <ul>
            |     <li><b>0.1.0-dev</b> Change listener for errors from PsiTreeChangeEvent to ProblemsListener<br>Problems Tab must be opened after indexing for it to work</li>
            |     <li><b>0.0.3</b> Fix the following error: <i>java.lang.IndexOutOfBoundsException: Wrong line: -1. Available lines count: 0</i></li>
            |     <li><b>0.0.2</b> Add ability to change error message text color and option to highlight the line.</li>
            |   </ul>
            | ]]>""".stripMargin
      },
      signPluginOptions := signPluginOptions.value.copy(
        enabled=true
      ),
      publishPlugin := (if (version.value.endsWith("dev")) "Dev" else "Stable")
    )
