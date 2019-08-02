#!/usr/bin/env bash

## Buiilds the OSX .app 

rm -rf target
mvn -DskipTests=true spring-javaformat:apply package
HERE=$(cd `dirname $0` && pwd )
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

archive_name=${app}.tgz

tar -c  $app  | gzip -9 > $archive_name 

