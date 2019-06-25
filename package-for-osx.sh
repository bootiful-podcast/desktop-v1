#!/usr/bin/env bash
# SEE https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javapackager.html

#export JAVA_HOME=`/usr/libexec/java_home -v 11`
export APP_NAME=BootifulPodcastDesktop
export APP_DIR_NAME=BootifulPodcastDesktop.app



IN=target/packager
OUT=target/out
mkdir -p $IN
mkdir -p $OUT

mvn -DskipTests=true spring-javaformat:apply clean package 
cp target/desktop-client.jar $IN

JRE_IMG_DIR=`pwd`/image 
rm -rf $JRE_IMG_DIR

jlink --add-modules=java.base --add-modules=java.desktop \
	--add-modules=java.net.http --add-modules=java.xml  \
	--add-modules=java.prefs --add-modules=java.logging  \
	--output $JRE_IMG_DIR


jlink --add-modules=java.base --add-modules=java.desktop \
	--add-modules=java.net.http --add-modules=java.xml --add-modules=java.prefs \
	--add-modules=java.logging  \
	--add-modules=javafx.base --add-modules=javafx.controls \
	--add-modules=javafx.fxml --add-modules=javafx.graphics \
	--add-modules=javafx.web --add-modules=javafx.media \
	--add-modules=javafx.swing --output $JRE_IMG_DIR