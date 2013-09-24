#!/bin/sh
#
APP_HOME=`(pwd)`
TARGET_DIR=$APP_HOME/appserver-release/target/appserver-release-1.0-distribution
#DATE_TIME=`date +%y_%m_%d-%k_%M`
COMPONENT="ContactServer"

echo "building $COMPONENT"
cd $APP_HOME
mvn -DskipTests package
mvn -DskipTests install

if [ ! -d $TARGET_DIR ]; then
    echo "$TARGET_DIR do not existent!"
    exit;
fi

#move the sql file
cp script/create_schema.sql $TARGET_DIR


#package files
RELEASE_PACKAGE_FILE=$APP_HOME/$COMPONENT.tar.gz
echo "packing $RELEASE_PACKAGE_FILE"
cd $TARGET_DIR
tar -czf $RELEASE_PACKAGE_FILE .
cd $APP_HOME
echo "Done"
