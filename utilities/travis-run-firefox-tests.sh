#!/bin/bash
set -ev

#############################################################
# This script updates travis with firefox and
# geckodriver and starts the selion firefox suites locally
#############################################################

if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
fi

# Remove any existing firefox data
if [ "${TRAVIS}" = true ]; then
  rm -rf ${HOME}/.mozilla
fi

mvn test -pl client -B -V -DsuiteXmlFile=Firefox-Suite.xml \
  -DSELION_SELENIUM_RUN_LOCALLY=true -DSELION_DOWNLOAD_DEPENDENCIES=true -B -V
exit $?
