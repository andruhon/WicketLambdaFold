# WicketLambdaFold

![Build](https://github.com/andruhon/WicketLambdaFold/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/9444.svg)](https://plugins.jetbrains.com/plugin/9444)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/9444.svg)](https://plugins.jetbrains.com/plugin/9444)

<!-- Plugin description -->
A plugin for IntelliJ IDEA which
folds/shortens wicket lambda model's getter and setter references.
So LambdaModel.of(model, Entity::getSomething, Entity::setSomething)
becomes LambdaModel.of(model, Entity::get/setSomething).

Usages of PropertyModel highlighted as warnings.

Also adds intentions to create HTML and .properties files for Wicket panels and pages.
<!-- Plugin description end -->

## Rationale

Some wicket teams consider PropertyModel harmful because it is not type safe.
Sometimes in bigger projects it's really hard to maintain all property models when methods get renamed.
LambdaModel and model maps help to reference properties via function references which are easily refactored by IDEs,
and nicely spotted by compilers.

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "WicketLambdaFold"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/andruhon/WicketLambdaFold/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
