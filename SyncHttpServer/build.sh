#!/bin/sh
#
APP_HOME=`(pwd)`
CONNECTOR_S4J=$APP_HOME/json-connector/target/json-connector-0.1.s4j
OFFICER_S4J=$APP_HOME/json-officer/target/json-officer-0.1.s4j
#DATE_TIME=`date +%y_%m_%d-%k_%M`
COMPONENT="SyncServer"

echo "building $COMPONENT"
cd $APP_HOME
mvn -DskipTests package

if [ ! -e $CONNECTOR_S4J ]; then
    echo "target file $CONNECTOR_S4J do not existent!"
    exit;
fi

if [ ! -e $OFFICER_S4J ]; then
    echo "target file $CONNECTOR_S4J do not existent!"
    exit;
fi

#package files
RELEASE_PACKAGE_FILE=$APP_HOME/$COMPONENT.tar.gz
echo "packing $RELEASE_PACKAGE_FILE"
cd $APP_HOME
cp $CONNECTOR_S4J json-connector-0.1.s4j
cp $OFFICER_S4J json-officer-0.1.s4j
tar -czf $RELEASE_PACKAGE_FILE json-connector-0.1.s4j json-officer-0.1.s4j
rm json-officer-0.1.s4j json-connector-0.1.s4j
cd $APP_HOME
echo "Done"
