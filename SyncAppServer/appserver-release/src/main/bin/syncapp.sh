#!/bin/sh
unset SYNC_APP_HOME
unset JAVA_OPTS

cd `dirname $0`
SYNC_APP_HOME=`(cd .. ; pwd)`
PID_FILE=/tmp/.sync_app.pid

echo $SYNC_APP_HOME

if [ ! -d $SYNC_APP_HOME/config ]; then
    echo "Missing config for sync application server start up."
    exit;
fi

if [ -z "$JAVA_HOME" ]; then
    echo "Missing java runtome, please check $JAVA_HOME"
    exit;
else
    if [ ! -f "$JAVA_HOME/bin/java" ]; then
        echo "Incorrect JRE path '$JAVA_HOME'."
        exit;
    fi
fi


case $1 in
start)
    #memory setting
    JAVA_OPTS="-Xmx512M"

    #debug
    RUN_DEBUG="false"
    if [ "debug"x = "$2"x ] ; then
        RUN_DEBUG="true"
        echo "Running in debug mode."
    fi
    JAVA_OPTS="$JAVA_OPTS -Dsync.app.debug=$RUN_DEBUG"
    JAVA_OPTS="$JAVA_OPTS -Dsync.app.home=$SYNC_APP_HOME"

    LIB_DIR=$SYNC_APP_HOME/lib
    JAR_FILES=`find $LIB_DIR -name *.jar -type f`
    SYNC_CLASS_PATH=$CLASSPATH
    for jar_file in ${JAR_FILES} ; do
        SYNC_CLASS_PATH="$jar_file:$SYNC_CLASS_PATH"
    done

    SYNC_CLASS_PATH=$LIB_DIR/information-api-1.1.0.jar:$LIB_DIR/jackson-core-asl-1.8.3.jar:$SYNC_CLASS_PATH


    if [ -f $PID_FILE ] ; then
        echo "FATAL error, one more server is runing in process: "`cat $PID_FILE`
	exit;
    fi

    LOG_DIR=$SYNC_APP_HOME/log
    if [ ! -d $LOG_DIR ] ; then
        mkdir $LOG_DIR
    fi

    nohup $JAVA_HOME/bin/java $JAVA_OPTS -classpath $SYNC_CLASS_PATH com.borqs.sync.server.framework.SyncAppServer >> /dev/null&
;;

stop)
    if [ -f  $PID_FILE ] ; then
        cat $PID_FILE | while read line
        do
            exec kill -9 $line
        done
        exec rm -rf $PID_FILE 
    fi
;;

*)
    echo "usage: syncapp.sh start|stop [debug]"
    exit;
esac
