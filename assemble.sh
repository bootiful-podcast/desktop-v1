#!/usr/bin/env bash

mvn -DskipTests=true spring-javaformat:apply clean package


HERE=$(cd `dirname $0` && pwd )

echo $HERE
BP_DESKTOP=${HERE}/target/BP
echo "BP_DESKTOP:  $BP_DESKTOP"
rm -rf ${BP_DESKTOP}
mkdir -p ${BP_DESKTOP}

cp ${HERE}/target/desktop.jar ${BP_DESKTOP}/desktop.jar
cp ${HERE}/run.sh ${BP_DESKTOP}/run.sh
cd ${BP_DESKTOP}
ls -la
pwd

APPNAME="BootifulPodcast"
DIR="${BP_DESKTOP}/${APPNAME}.app/Contents/MacOS"
mkdir -p "${DIR}"
cp ${HERE}/run.sh "$DIR/$APPNAME"
cp ${HERE}/target/desktop.jar "$DIR/desktop.jar"
chmod +x "$DIR/$APPNAME"
