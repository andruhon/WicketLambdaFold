# WicketLambdaFold

![Build](https://github.com/andruhon/WicketLambdaFold2/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/9444.svg)](https://plugins.jetbrains.com/plugin/9444)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/9444.svg)](https://plugins.jetbrains.com/plugin/9444)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [x] Verify the [pluginGroup](/gradle.properties), [plugin ID](/src/main/resources/META-INF/plugin.xml) and [sources package](/src/main/kotlin).
- [x] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [x] Set the Plugin ID in the above README badges.
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
A plugin for IntelliJ IDEA which
folds/shortens wicket lambda model's getter and setter references.
So LambdaModel.of(model, Entity::getSomething, Entity::setSomething)
becomes LambdaModel.of(model, Entity::get/setSomething).

Usages of PropertyModel highlighted as warnings.

Also adds intentions to create HTML and .properties files for Wicket panels and pages.
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "WicketLambdaFold"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/andruhon/WicketLambdaFold2/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
