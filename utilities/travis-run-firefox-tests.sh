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

mkdir -p target
cd target
if [ ! -f "./firefox/firefox" ]
then
  export FIREFOX_VERSION=57.0
  export FIREFOX_URL=http://download.cdn.mozilla.net/pub/firefox/releases/${FIREFOX_VERSION}/linux-x86_64/en-US/firefox-${FIREFOX_VERSION}.tar.bz2
  wget -O firefox-${FIREFOX_VERSION}.tar.bz2 ${FIREFOX_URL}
  # and install downloaded firefox
  tar -xjf firefox-${FIREFOX_VERSION}.tar.bz2
fi
cd ..

export PATH=${PWD}/target/firefox:${PATH}
mvn test -pl client -B -V -DsuiteXmlFile=Firefox-Suite.xml -DSELION_BROWSER=firefox-headless \
  -DSELION_SELENIUM_RUN_LOCALLY=true -DSELION_DOWNLOAD_DEPENDENCIES=true -B -V
exit $?
