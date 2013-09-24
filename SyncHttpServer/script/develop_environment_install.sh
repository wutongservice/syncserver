#!/bin/sh

cd `dirname $0`
REL_HOME=`(pwd)`

FUNAMBOL_HOME=$HOME/release/contacts_service/Funambol
FUNAMBOL_BIN=$FUNAMBOL_HOME/bin
FUNAMBOL_MODULE_HOME=$FUNAMBOL_HOME/ds-server/modules/
JSON_CONNECTOR_NAME=json-connector-0.1.s4j
JSON_OFFICER_NAME=json-officer-0.1.s4j
FUNAMBOL_MODULE_JSONCONNECTOR=$FUNAMBOL_MODULE_HOME/$JSON_CONNECTOR_NAME
FUNAMBOL_MODULE_JSONOFFICER=$FUNAMBOL_MODULE_HOME/$JSON_OFFICER_NAME

MODULE_SOURCE=json_module_source

#the funation to kill process
kill_process()
{
for pid in `cat $1`
do
{
echo "$pid"
kill -9 $pid
}
done

rm -f $1
}



if [ -z "$1" ] ; then  
    echo "please enter your package name want to be installed"
    exit;
fi


if [ ! -d $FUNAMBOL_HOME ] ; then
     echo "$FUNAMBOL_HOME don't exist,exit"
exit;
fi

if [ ! -d $FUNAMBOL_BIN ] ; then
    echo "$FUNAMBOL_BIN don't exist,exit"
exit;
fi

#stop funambol
cd $FUNAMBOL_BIN
echo "stop funambol......"
./funambol stop
echo "funambol stop finished"

cd $REL_HOME

#decompress the tar package
if [ ! -d $MODULE_SOURCE ] ; then
mkdir $MODULE_SOURCE
fi

echo  "decompress the $1"
tar -zxvf $1 -C $MODULE_SOURCE
echo  "decompress $1 finished"

#check the jsonconnector and jsonofficer if exists
if [ ! -f $MODULE_SOURCE/$JSON_CONNECTOR_NAME ] ; then
    echo "no $MODULE_SOURCE/$JSON_CONNECTOR_NAME,please make sure that the tar package is correct!"
    exit;
fi
if [ ! -f $MODULE_SOURCE/$JSON_OFFICER_NAME ] ; then
    echo "no $MODULE_SOURCE/$JSON_OFFICER_NAME,please make sure that the tar package is correct!"
    exit;
fi

#backup the module
if [ -f $MODULE_SOURCE/$FUNAMBOL_MODULE_JSONCONNECTOR ] ; then
    echo "backup the $FUNAMBOL_MODULE_JSONCONNECTOR"
    mv $MODULE_SOURCE/$FUNAMBOL_MODULE_JSONCONNECTOR $FUNAMBOL_MODULE_HOME/json-connector-0.1.s4j.bak
fi
if [ -f $MODULE_SOURCE/$FUNAMBOL_MODULE_JSONCONNECTOR ] ; then
    echo "backup the $FUNAMBOL_MODULE_JSONCONNECTOR"
    mv $MODULE_SOURCE/$FUNAMBOL_MODULE_JSONCONNECTOR $FUNAMBOL_MODULE_HOME/json-connector-0.1.s4j.bak
fi

#replace the jsonconnector and jsonofficer package 
echo "copy the decompressed jsonconnector and jsonofficer......"
cp $REL_HOME/$MODULE_SOURCE/$JSON_CONNECTOR_NAME $FUNAMBOL_MODULE_HOME/$JSON_CONNECTOR_NAME
cp $REL_HOME/$MODULE_SOURCE/$JSON_OFFICER_NAME $FUNAMBOL_MODULE_HOME/$JSON_OFFICER_NAME

#install the module
cd $FUNAMBOL_BIN
./install-modules

#start the server
cd $FUNAMBOL_BIN

#kill the exist funambol
echo "kill the running funambol process"
ps aux | grep "funambol" | grep -v "grep" | awk '{print $2}' > /tmp/funambol.pid
kill_process "/tmp/funambol.pid"

echo "start funambol......"
nohup ./funambol start >> /dev/null&














