#!/usr/bin/env bash

## Builds the OSX .app
export BP_MODE=${BP_MODE:-development}
mkdir -p target/macos
HERE=$(cd `dirname $0` && pwd )
T=target
app_name=BootifulPodcast
app=target/macos/${app_name}.app
contents=${app}/Contents/
mac_os=${contents}/MacOS
resources=${contents}/Resources
mkdir -p $mac_os
mkdir -p $resources

echo ${BP_MODE} > ${mac_os}/bp_mode
cp -r ${HERE}/assembly/run.sh ${mac_os}/run.sh
cp -r ${HERE}/target/desktop.jar ${mac_os}/desktop.jar
cp -r ${HERE}/assembly/app.icns ${resources}/app.icns
cp -r ${HERE}/assembly/Info.plist ${contents}/Info.plist
chmod +x $app
touch $app

archive_name=${app}.tgz

tar -c  $app  | gzip -9 > $archive_name
echo "the application has been saved into ${archive_name}."

