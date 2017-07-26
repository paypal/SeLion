#!/bin/bash
set -ev

#############################################################
# This script updates travis CI with chromedriver and starts
# the selion chrome suite locally
#############################################################

if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
fi

mvn test -pl client -DsuiteXmlFile=Chrome-Suite.xml \
  -DSELION_SELENIUM_RUN_LOCALLY=true -DSELION_DOWNLOAD_DEPENDENCIES=true \
  -DSELION_SELENIUM_CUSTOM_CAPABILITIES_PROVIDER=com.paypal.selion.TravisCICapabilityBuilder \
  -DBROWSER_PATH=`which google-chrome` -B -V
exit $?
