#!/bin/bash
set -ev
if [ -n "${SAUCE_USERNAME}" ]; then
  echo { \"sauceUserName\": \"${SAUCE_USERNAME}\", \"sauceApiKey\": \"${SAUCE_ACCESS_KEY}\", \"tunnel-identifier\": \"__string__${TRAVIS_JOB_NUMBER}\", \"build\": \"${TRAVIS_BUILD_NUMBER}\", \"idle-timeout\": 120, \"tags\": [\"commit ${TRAVIS_COMMIT}\", \"branch ${TRAVIS_BRANCH}\", \"pull request ${TRAVIS_PULL_REQUEST}\"] } > client/src/test/resources/sauceConfig.json
  mvn test -Djava.net.preferIPv4Stack=true
else
  mvn test -Djava.net.preferIPv4Stack=true -DsuiteXmlFile=PhantomJS-Suite.xml -DSELION_SELENIUM_RUN_LOCALLY=true
fi
