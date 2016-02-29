#!/bin/bash
#Deploys resulting artifacts to sonatype nexus
if [ "$TRAVIS_REPO_SLUG" = "paypal/SeLion" ] && [ "$TRAVIS_JDK_VERSION" = "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "$TRAVIS_BRANCH" = "develop" ]; then
    echo "Deploying to Sonatype...\n"
    mvn -s utilities/settings.xml deploy -B -V
fi
