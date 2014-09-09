#!/bin/bash
git clone --depth=50 https://github.com/ios-driver/ios-driver.git ios-driver/ios-driver
cd ios-driver/ios-driver
git submodule init
git submodule update
mvn install -am -pl server,grid -DskipTests=true -Dmaven.javadoc.skip=true -B -V
