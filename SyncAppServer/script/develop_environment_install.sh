cd `dirname $0`
REL_HOME=`(pwd)`

SYNC_APP_HOME=$HOME/release/contacts_service/SyncAppServer
SYNC_APP_HOME_BIN_NAME=bin
SYNC_APP_HOME_CONFIG_NAME=config
SYNC_APP_HOME_LIB_NAME=lib

SYNC_APP_RELEASE_SOURCE=appserver-release-1.0-distribution

#check the target folder
if [ ! -d $HOME/release ] ; then
  echo "$HOME/release doesn't exist,will make it."
  mkdir $HOME/release
  mkdir $HOME/release/contacts_service
  mkdir $SYNC_APP_HOME
fi

if [ ! -d $HOME/release/contacts_service ] ; then
  echo "$HOME/release/contacts_services doesn't exist,will make it."
  mkdir $HOME/release/contacts_service
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
cp -rf $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_BIN_NAME $SYNC_APP_HOME
cp -rf $SYNC_APP_RELEASE_SOURCE/$SYNC_APP_HOME_LIB_NAME $SYNC_APP_HOME

#update the config,default is development environment
SHOME=$HOME
CMHOME=${SHOME//\//\\/}

cd $SYNC_APP_HOME/$SYNC_APP_HOME_CONFIG_NAME
#1.update the webagent.properties
cp webagent.properties webagent.properties.bak
sed "s/webagent_conf_path=\/home\/sync_contacts\/tmp\/.webagent-conf\//webagent_conf_path=$CMHOME\/tmp\/.webagent-conf\//g" webagent.properties.bak >  webagent.properties
rm -rf webagent.properties.bak
cat webagent.properties

cd $SYNC_APP_HOME/$SYNC_APP_HOME_BIN_NAME
echo "Start server..."
sh syncapp.sh start 





