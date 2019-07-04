#!/usr/bin/env bash

HERE=$(cd `dirname $0` && pwd )
echo $HERE
export BP_DESKTOP=$HERE/target/package
rm -rf ${BP_DESKTOP}
mkdir -p ${BP_DESKTOP}
mvn -DskipTests=true spring-javaformat:apply clean package
cp ${HERE}/target/desktop.jar ${BP_DESKTOP}
cp ${HERE}/target/run.sh ${BP_DESKTOP}
cd ${BP_DESKTOP}/target
ls -la
pwd
APPNAME="BootifulPodcast"
DIR="${APPNAME}.app/Contents/MacOS"
mkdir -p "${DIR}"
cp ${HERE}/run.sh "$DIR/$APPNAME"
cp ${HERE}/target/desktop.jar "$DIR/$APPNAME"
chmod +x "$DIR/$APPNAME"
