# Release process
 - Make sure GitHub repository has a `PUBLISH_TOKEN` secret in Secrets section of Settings
 - Push a commit to GitHub
 - Watch the build in Actions "Build" workflow
 - A release draft will be created in tags section automatically when build is successful
 - Publish the release draft
    - Publishing release will cause another workflow run in "Release" section of Actions,
        if everything is OK the plugin will be published to Jetbrains marketplace

## Publish token
 see https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html and https://plugins.jetbrains.com/author/me/tokens
