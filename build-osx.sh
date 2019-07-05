#!/usr/bin/env bash


rm -rf target
mvn -DskipTests=true spring-javaformat:apply package

HERE=$(cd `dirname $0` && pwd )

#echo "Here is $HERE"
#
#BP_DESKTOP=${HERE}/target/macOS
#rm -rf ${BP_DESKTOP}
#mkdir -p ${BP_DESKTOP}
#
#cp ${HERE}/target/desktop.jar ${BP_DESKTOP}/desktop.jar
#cp ${HERE}/assembly/run.sh ${BP_DESKTOP}/run.sh
#cd ${BP_DESKTOP}
#ls -la
#pwd
#
#
#APPNAME="BootifulPodcast"
#CONTENTS=${BP_DESKTOP}/${APPNAME}.app/Contents
#DIR="${CONTENTS}/MacOS"
#mkdir -p "${DIR}"
#cp ${HERE}/assembly/run.sh "$DIR/$APPNAME"
#cp ${HERE}/target/desktop.jar "$DIR/desktop.jar"
#mkdir -p ${CONTENTS}/Resources
#cp ${HERE}/assembly/app.icns ${CONTENTS}/Resources/${APPNAME}.icns
#
#chmod +x "$DIR/$APPNAME"
#

T=target
app_name=BootifulPodcast
app=target/${app_name}.app
contents=${app}/Contents/
mac_os=${contents}/MacOS
resources=${contents}/Resources
mkdir -p $mac_os
mkdir -p $resources

cp -r ${HERE}/assembly/run.sh ${mac_os}/run.sh
cp -r ${HERE}/target/desktop.jar ${mac_os}/desktop.jar
cp -r ${HERE}/assembly/app.icns ${resources}/app.icns
cp -r ${HERE}/assembly/Info.plist ${contents}/Info.plist

chmod +x $app
touch $app
#sudo killall Finder && sudo killall Finder

#
#➜  BootifulPodcast.app git:(master) ✗ tree
#.
#└── Contents
#    ├── MacOS
#    │   ├── BootifulPodcast
#    │   └── desktop.jar
#    └── Resources
#        └── BootifulPodcast.icns
