#!/bin/bash
#Generate the javadocs only if the following conditions are satisfied
#the build is for the project paypal\SeLion, not on the fork
#the project is built on oraclejdk7
#the build is not on a pull request
#the build is on the develop branch
if [ "$TRAVIS_REPO_SLUG" = "paypal/SeLion" ] && [ "$TRAVIS_JDK_VERSION" = "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "$TRAVIS_BRANCH" = "develop" ]; then
	echo "Publishing javadocs...\n"
	cd $HOME
	#update user email and name in git config for travis
	git config --global user.email "travis@travis-ci.org"
	git config --global user.name "travis-ci"
	# clone the project using TOKEN
	git clone --quiet https://${GH_TOKEN}@github.com/paypal/SeLion > /dev/null
	cd SeLion
	sh utilities/push-javadoc-to-gh-pages.sh
	echo "Published Javadoc to gh-pages branch.\n"
else
	echo "Failed to publish Javadocs to gh-pages branch as one or more validations against Travis environment variables failed.\n"
fi