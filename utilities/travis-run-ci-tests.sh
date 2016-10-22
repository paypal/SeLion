#!/bin/bash
set -ev

#########################################################
# This script runs the tests either locally with
# phantomjs or remotely against sauce labs depending on
# CI configuration / presence of environment variables
#########################################################

if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
  mvn test -DSELION_BROWSER_CAPABILITY_PLATFORM=WINDOWS -B -V
else
  mvn test -DsuiteXmlFile=PhantomJS-Suite.xml -DSELION_SELENIUM_RUN_LOCALLY=true -B -V
fi
