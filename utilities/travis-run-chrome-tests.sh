#!/bin/bash
set -ev

#############################################################
# This script updates travis CI with chromedriver and starts
# the selion chrome suite locally
#############################################################

if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
fi

if [ "$USE_SAUCELABS" = true ]; then
  mvn test -pl client -B -V -DsuiteXmlFile=Chrome-Suite.xml -DSELION_BROWSER_RUN_HEADLESS=true && exit $?
else
  mvn test -pl client -B -V -DsuiteXmlFile=Chrome-Suite.xml -DSELION_BROWSER_RUN_HEADLESS=true \
    -DSELION_SELENIUM_RUN_LOCALLY=true -DSELION_DOWNLOAD_DEPENDENCIES=true && exit $?
fi
