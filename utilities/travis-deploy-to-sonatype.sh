#!/bin/bash
################################################
# Deploys resulting artifacts to sonatype nexus
################################################

# Deploy only if the following conditions are satisfied
# 1. The build is for the project paypal/SeLion, not on the fork
# 2. The project is built on oraclejdk8
# 3. The build is not on a pull request
# 4. The build is on a develop branch

ISDEVELOP=false

check_branch() {
  for developBranch in develop develop-1.2.0
  do
    if [ "$TRAVIS_BRANCH" = "$developBranch" ]; then
      ISDEVELOP=true
    fi
  done
}

check_branch
if [ "$TRAVIS_REPO_SLUG" = "paypal/SeLion" ] && [ "$TRAVIS_JDK_VERSION" = "oraclejdk8" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "$ISDEVELOP" = true ]; then
    echo "Deploying to Sonatype...\n"
    # checkout the TRAVIS_BRANCH branch
    git checkout $TRAVIS_BRANCH
    # verify that we are on TRAVIS_BRANCH branch, otherwise exit with error code
    output=$(git rev-parse --abbrev-ref HEAD)
    if [ "$output" = "$TRAVIS_BRANCH" ]; then
        # 'clean' must also be invoked here or the SeLion-Archetype build will fail
        # due to the previous output from the maven-invoker-plugin
        mvn clean deploy -DskipTests=true --settings utilities/settings.xml -B -V
    else
        echo "Failed to switch into $TRAVIS_BRANCH branch."
        exit 1
    fi
else
    echo "Deployment selection criteria not met."
fi
