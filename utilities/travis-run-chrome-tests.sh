#!/bin/bash
set -e

#############################################################
# This script updates travis CI with chromedriver and starts
# the selion chrome suite locally
#############################################################
if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
fi

mkdir -p target
cd target
if [ ! -f "./google-chrome" ]; then
  export CHROME_REVISION=`curl -s http://commondatastorage.googleapis.com/chromium-browser-snapshots/Linux_x64/LAST_CHANGE`
  curl -L -O "http://commondatastorage.googleapis.com/chromium-browser-snapshots/Linux_x64/${CHROME_REVISION}/chrome-linux.zip"
  unzip -o chrome-linux.zip
  ln -sf $PWD/chrome-linux/chrome-wrapper google-chrome
fi

./google-chrome -version

if [ ! -f "./chromedriver" ]; then
  export CHROMEDRIVER_VERSION=2.24
  curl -L -O "http://chromedriver.storage.googleapis.com/${CHROMEDRIVER_VERSION}/chromedriver_linux64.zip"
  unzip -o chromedriver_linux64.zip && chmod +x chromedriver
fi
cd ..

export PATH=$PWD/target:$PATH
mvn test -pl client -DsuiteXmlFile=Chrome-Suite.xml \
  -DSELION_SELENIUM_RUN_LOCALLY=true \
  -DSELION_SELENIUM_CHROMEDRIVER_PATH=$PWD/target/chromedriver \
  -DSELION_SELENIUM_CUSTOM_CAPABILITIES_PROVIDER=com.paypal.selion.TestCapabilityBuilder \
  -DBROWSER_PATH=$PWD/target/google-chrome -B -V
