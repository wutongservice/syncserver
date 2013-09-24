#!/bin/sh

#example command
# ./init-contacts-mappings.sh --host 192.168.5.208 --user borqs_sync --pwd borqs_sync --db borqs_sync --map apitest.borqs.com

ARGS_LEN=$#;
ARGS=$*;
echo "argument length is $ARGS_LEN, $ARGS";

MY_HOST="127.0.0.1";
MY_PORT=3306
MY_DB="syncservice";
MY_USER="root";
MY_PWD="root";
MAP_HOST="127.0.0.1";

CUR_ARG="";
for x in $ARGS
do
	if [[ $x =~ --.* ]]; then
		CUR_ARG="$x";
		echo "current arg is $CUR_ARG";
	else
		if [[ $CUR_ARG == --host ]]; then
			MY_HOST=$x;
			echo "mysql host is $MY_HOST";
		elif  [[ $CUR_ARG == --port ]]; then
			MY_PORT=$x;
			echo "mysql port is $MY_PORT";
		elif  [[ $CUR_ARG == --db ]]; then
			MY_DB=$x;
			echo "mysql DB is $MY_DB";
		elif  [[ $CUR_ARG == --user ]]; then
			MY_USER=$x;
			echo "mysql user is $MY_USER";
		elif  [[ $CUR_ARG == --pwd ]]; then
			MY_PWD=$x;
			echo "mysql password is $MY_PWD";
		elif  [[ $CUR_ARG == --map ]]; then
			MAP_HOST=$x;
			echo "mappings host is $MAP_HOST";
		else
			echo "The arg $CUR_ARG can't be recognized";
		fi
	fi
done

URL_FETCH_MAPPINGS="$MAP_HOST/sync/webagent/contacts/borqsids?formated=false&cols=cid,bid&oid="
echo $URL_FETCH_MAPPINGS;

c=0;
for uid in `mysql -h$MY_HOST -u$MY_USER -p$MY_PWD $MY_DB -B -e "SELECT distinct userid FROM borqs_pim_contact WHERE 1"`;do
	echo "current userid is $uid:";
	curl "$URL_FETCH_MAPPINGS$uid";
	echo;
	c=`expr $c + 1`;
done
echo "finish initializing contacts mappings, total items is $c.";
