#!/bin/bash
# The script portion pertaining to detecting the JDK installation and setting it up has been borrowed from
# http://www.gimlisys.com/articles-detect-java.html
REQUIRED_VERSION=1.7

DISPLAY_VERSION=$REQUIRED_VERSION

# Transform the required version string into a number that can be used in comparisons
REQUIRED_VERSION=`echo $REQUIRED_VERSION | sed -e 's;\.;0;g'`
# Check JAVA_HOME directory to see if Java version is adequate
if [ $JAVA_HOME ]
then
    JAVA_EXE=$JAVA_HOME/bin/java
    $JAVA_EXE -version 2> tmp.ver
    VERSION=`cat tmp.ver | grep "java version" | awk '{ print substr($3, 2, length($3)-2); }'`
    rm tmp.ver
    VERSION=`echo $VERSION | awk '{ print substr($1, 1, 3); }' | sed -e 's;\.;0;g'`
    if [ $VERSION ]
    then
        if [ $VERSION -ge $REQUIRED_VERSION ]
        then
            JAVA_HOME=`echo $JAVA_EXE | awk '{ print substr($1, 1, length($1)-9); }'`
        else
            JAVA_HOME=
        fi
    else
        JAVA_HOME=
    fi
fi

# If the existing JAVA_HOME directory is adequate, then leave it alone
# otherwise, use 'locate' to search for other possible java candidates and
# check their versions.
if [ $JAVA_HOME ]
then
    :
else
    for JAVA_EXE in `locate bin/java | grep java$ | xargs echo`
    do
        if [ $JAVA_HOME ] 
        then
            :
        else
            $JAVA_EXE -version 2> tmp.ver 1> /dev/null
            VERSION=`cat tmp.ver | grep "java version" | awk '{ print substr($3, 2, length($3)-2); }'`
            rm tmp.ver
            VERSION=`echo $VERSION | awk '{ print substr($1, 1, 3); }' | sed -e 's;\.;0;g'`
            if [ $VERSION ]
            then
                if [ $VERSION -ge $REQUIRED_VERSION ]
                then
                    JAVA_HOME=`echo $JAVA_EXE | awk '{ print substr($1, 1, length($1)-9); }'`
                fi
            fi
        fi
    done
fi

# If the correct Java version is detected, then export the JAVA_HOME environment variable
if [ $JAVA_HOME ]
then
    export JAVA_HOME
    echo $JAVA_HOME
else
    echo "Could not find an existing installation of Java version "  $DISPLAY_VERSION
    exit 1
fi

HUB_CONFIG="hubConfig.json"

if [ "$1" = "sauce" ]
then
HUB_CONFIG="hubSauceConfig.json"
fi

export CURRENT_DIR=`pwd`
export PATH=$JAVA_HOME/jre/bin:$JAVA_HOME/bin:$CURRENT_DIR:$PATH
java -DSeLionConfig=SeLionConfigNonWindows.json -cp *:. com.paypal.selion.utils.JarSpawner "java -DSeLionConfig=SeLionConfigNonWindows.json -Djava.util.logging.config.file=logs/logging.properties -cp *:. com.paypal.selion.grid.SeLionGridLauncher -role hub -hubConfig $HUB_CONFIG"
