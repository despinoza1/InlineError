# InlineError Plugin

Plugin for displaying errors message in the editor; inspired by Alexander's VSCode extension [Error Lens](https://marketplace.visualstudio.com/items?itemName=usernamehw.errorlens).

## Installing

### Stable

Install using **Plugins** tab in IDE Settings.

### DEV

Open IDE Settings and go to Plugins. Open plugin settings and select **Manage Plugin Repositories**. 
Add the following repository URL:

    https://plugins.jetbrains.com/plugins/dev/17302

After adding repo, you will get option to update to **dev** release.
More information: [Jetbrains Docs: Managing Plugins](https://www.jetbrains.com/help/idea/managing-plugins.html#repos)

To enable **DEBUG** logs go to **Help | Diagnostic Tools | Debug Log Settings** and add `#com.daniel_epsinoza.inline_error`.
More information: [Jetbrains Docs: IDE Infrastructure - Logging](https://plugins.jetbrains.com/docs/intellij/ide-infrastructure.html#7fa8e6c1)


### Local

Run SBT and execute one of the following tasks:
* runIDE - Opens IDE from *intellijPlatform* in [build.sbt](build.sbt) with plugin
* packageArtifactZip - Creates ZIP of plugin in **target** and do the following [install plugin from disk](https://www.jetbrains.com/help/idea/managing-plugins.html#install_plugin_from_disk)

More details of available tasks at [sbt-idea-plugin](https://github.com/JetBrains/sbt-idea-plugin).
