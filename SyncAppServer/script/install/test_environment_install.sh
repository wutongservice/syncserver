cd `dirname $0`
REL_HOME=`(pwd)`

SYNC_APP_HOME=/root/bcs/SyncAppServer
SYNC_APP_HOME_BIN_NAME=bin
SYNC_APP_HOME_CONFIG_NAME=config
SYNC_APP_HOME_LIB_NAME=lib

SYNC_APP_RELEASE_SOURCE=appserver-release-1.0-distribution

#check the target folder
if [ ! -d $HOME/bcs ] ; then
  echo "$HOME/bcs doesn't exist,will make it."
  mkdir $HOME/bcs
  mkdir $SYNC_APP_HOME
fi

if [ ! -d $SYNC_APP_HOME ] ; then
  echo "$SYNC_APP_HOME doesn't exist,will make it."
  mkdir $SYNC_APP_HOME
fi

cd $REL_HOME

if [ -z "$1" ] ; then  
    echo "please enter your package name want to be installed"
    exit;
fi

#decompress the *.tar.gz
if [ ! -d $SYNC_APP_RELEASE_SOURCE ] ; then
mkdir $SYNC_APP_RELEASE_SOURCE
fi

echo "decompress the $1"
tar -zxvf $1 -C $SYNC_APP_RELEASE_SOURCE

#execute the sql statement
if [ ! -f $SYNC_APP_RELEASE_SOURCE/create_schema.sql ] ; then
echo "can not find the sql file:create_schema.sql"
exit;
fi

mysql -h apptest4.borqs.com -uroot -pQ1w2e3r4 < $SYNC_APP_RELEASE_SOURCE/create_schema.sql

#check the decompressed is correct
if [ ! -d $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_BIN_NAME ] ; then
    echo "$SYNC_APP_HOME_BIN_NAME doesn't exist,please check the $1 if correct"
    exit;
fi

if [ ! -d $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_CONFIG_NAME ] ; then
    echo "$SYNC_APP_HOME_CONFIG_NAME doesn't exist,please check the $1 if correct"
    exit;
fi

if [ ! -d $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_LIB_NAME ] ; then
    echo "$SYNC_APP_HOME_LIB_NAME doesn't exist,please check the $1 if correct"
    exit;
fi

#stop the syncapp server
if [ ! -d $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME ] ; then
    mkdir $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME
else
    cd $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME
    echo "stop server.."
    sh syncapp.sh stop
    cd $REL_HOME
fi

#copy the resource
rm -rf $SYNC_APP_HOME/$SYNC_APP_HOME_LIB_NAME
rm -rf $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME
rm -rf $SYNC_APP_HOME/$SYNC_APP_HOME_CONFIG_NAME
cp -rf $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_BIN_NAME $SYNC_APP_HOME
cp -rf $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_LIB_NAME $SYNC_APP_HOME
cp -rf $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_CONFIG_NAME  $SYNC_APP_HOME

#update the config,default is development environment
#SHOME=$HOME
#CMHOME=${SHOME//\//\\/}

#echo "CMHOME: $CMHOME"
cd $SYNC_APP_HOME/$SYNC_APP_HOME_CONFIG_NAME
#1.update the webagent.properties
cp webagent.properties webagent.properties.bak
sed 's/webagent_conf_path=\/home\/sync_contacts\/tmp\/.webagent-conf\//webagent_conf_path=\/root\/tmp\/.webagent-conf\//g' webagent.properties.bak >  webagent.properties
rm -rf webagent.properties.bak
cat webagent.properties


#2.update the server.properties
cp server.properties server.properties.bak
#TODO no test sms_service_number now,use development as it.
sed -e 's/sms_service_number=15910288426/sms_service_number=15910288426/g' -e 's/naming_service_port=8999/naming_service_port=8999/g' -e 's/namign_service_host=127.0.0.1/namign_service_host=apptest7.borqs.com/g' -e 's/account_server_host=http:\/\/apitest.borqs.com/account_server_host=http:\/\/apptest1.borqs.com/g' server.properties.bak > server.properties
rm -rf server.properties.bak
cat server.properties

#3.update the push.properties
cp pushservice.properties pushservice.properties.bak
sed 's/app1.borqs.com:9090\/plugins\/xDevice\/send/apptest2.borqs.com:9090\/plugins\/xDevice\/send/g' pushservice.properties.bak > pushservice.properties
rm -rf pushservice.properties.bak
cat pushservice.properties

#4.update the jms.properties
cp jms.properties jms.properties.bak
sed -e 's/username=syncserver/username=borqs_sync/g' -e 's/password=borqs.com/password=borqs_sync/g' -e 's/url=tcp:\/\/192.168.5.208:61616/url=tcp:\/\/apptest3.borqs.com:61616/g' jms.properties.bak > jms.properties
rm -rf jms.properties.bak
cat jms.properties

#5.information.properties
cp information.properties information.properties.bak
sed 's/url=avro:\/\/192.168.5.208:8083/url=avro:\/\/apptest3.borqs.com:8083/g' information.properties.bak > information.properties
rm -rf information.properties.bak
cat information.properties

#6.database.properties
cp database.properties database.properties.bak
sed -e 's/password=root/password=Q1w2e3r4/g' -e 's/url=jdbc:mysql:\/\/localhost:3306\/borqs_sync?characterEncoding=UTF-8/url=jdbc:mysql:\/\/apptest4.borqs.com:3306\/borqs_sync?characterEncoding=UTF-8/g' database.properties.bak > database.properties
rm -rf database.properties.bak
cat database.properties


cd $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME
echo "Start server..."
sh syncapp.sh start 




