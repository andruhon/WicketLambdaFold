<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# WicketLambdaFold Changelog

## [Unreleased]
## [0.5.0]
### Added
- Rebuilt plugin using [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Replaced deprecated API uses
- Added quick fix for chained PropertyModels ie
  `new PropertyModel(model, 'user.info.name')` -> `model.map(Data::getUser).map(User::getInfo).map(Info::getName)`

