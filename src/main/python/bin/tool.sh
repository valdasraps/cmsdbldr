#!/bin/bash

cd "$(dirname "$0")"

aopt=0
ropt=0

# Checking and handling options and variables

while getopts ":ar" opt; do
  case $opt in
    a)
      aopt=1
      ;;
    r)
      ropt=1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
  esac
done

shift $(($OPTIND - 1))
det=$1
dat=$2

if [[ ( 0 -eq $aopt  &&  0 -eq $ropt ) || ( 1 -eq $aopt && 1 -eq $ropt ) || ! -n "$det" || ! -n "$dat" ]]; then
    echo "Usage: $0 -a|-r detector database" >&2
    exit 1
fi

grep --quiet CentOS /etc/system-release
CENTOS=$?

DBSPOOL=/home/dbspool
DETDAT=$det/$dat
PROPS=/opt/cmsdbldr/properties/${det}_${dat}.properties

# Removing whatever was created before...

rm -fR $DBSPOOL/spool/$DETDAT $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
tf=`mktemp`
cat /var/spool/incron/dbspool | grep -v "$DBSPOOL/spool/$DETDAT/" >$tf
mv $tf /var/spool/incron/dbspool

if [ -f /etc/httpd/conf.d/cmsdbldr_${det}_${dat}.conf ]; then

  rm /etc/httpd/conf.d/cmsdbldr_${det}_${dat}.conf
  systemctl reload httpd

fi

if [ -f /etc/systemd/system/cmsdbldr_${det}_${dat}.service ]; then

  systemctl stop cmsdbldr_${det}_${dat}
  rm -f /etc/systemd/system/cmsdbldr_${det}_${dat}.service
  systemctl daemon-reload

fi

# Adding again if requested

if [[ 1 -eq $aopt ]]; then

  mkdir -p $DBSPOOL/spool/$DETDAT $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
  chown -R dbspool:dbspool $DBSPOOL/spool/$det $DBSPOOL/state/$det $DBSPOOL/logs/$det
  chmod 755 $DBSPOOL/spool/$det $DBSPOOL/state/$det $DBSPOOL/logs/$det
  chmod 755 $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
  chmod 777 $DBSPOOL/spool/$DETDAT

  touch $PROPS
  chown dbspool:dbspool $PROPS

  echo "$DBSPOOL/spool/$DETDAT IN_CREATE,IN_MOVED_TO /opt/cmsdbldr/bin/runner.sh $DBSPOOL/spool/$DETDAT/\$#" >>/var/spool/incron/dbspool

  grep --quiet "^[!#]*api-port=[0-9]\+" $PROPS
  ISAPI=$?

  if [ "$ISAPI" == 0 ]; then

    API_PORT=`grep "^[!#]*api-port=[0-9]\+" $PROPS | sed 's/^[!#]*api-port=\([0-9]\+\)[^0-9]*$/\1/g'`

cat << EOF > /etc/httpd/conf.d/cmsdbldr_${det}_${dat}.conf
<Location /${det}/${dat}>

  SSLRequireSSL
  AuthType shibboleth
  ShibRequestSetting requireSession 1
  require shib-session
  ShibUseHeaders On

  RequestHeader add "ADFS-LOGIN" "%{ADFS_LOGIN}e" "env=ADFS_LOGIN"
  RequestHeader add "ADFS-FULLNAME" "%{ADFS_FULLNAME}e" "env=ADFS_FULLNAME"
  RequestHeader add "ADFS-GROUP" "%{ADFS_GROUP}e" "env=ADFS_GROUP"

  ProxyPass http://127.0.0.1:$API_PORT
  ProxyPassReverse http://127.0.0.1:$API_PORT
</Location>
EOF

  if [ "$CENTOS" == 0 ]; then

cat << EOF > /etc/systemd/system/cmsdbldr_${det}_${dat}.service
[Unit]
Description=Start and stop CMS DB Loader service for ${det} at ${dat}

[Service]
Type=simple
TimeoutSec=10sec
PIDFile=/var/run/cmsdbldr_${det}_${dat}.pid
ExecStart=/usr/bin/jsvc -server -pidfile /var/run/cmsdbldr_${det}_${dat}.pid -outfile /var/cmsdbldr/rest_${det}_${dat}.out.log -errfile /var/cmsdbldr/rest_${det}_${dat}.err.log -wait 30 -cp /opt/cmsdbldr/bin/cmsdbldr.jar -Doracle.net.tns_admin=/etc org.cern.cms.dbloader.rest.Application $PROPS
ExecStop=/usr/bin/jsvc -server -pidfile /var/run/cmsdbldr_${det}_${dat}.pid -outfile /var/cmsdbldr/rest_${det}_${dat}.out.log -errfile /var/cmsdbldr/rest_${det}_${dat}.err.log -wait 30 -stop -cp /opt/cmsdbldr/bin/cmsdbldr.jar -Doracle.net.tns_admin=/etc org.cern.cms.dbloader.rest.Application $PROPS

EOF

    systemctl daemon-reload
    systemctl restart incrond
    systemctl restart httpd
    systemctl start cmsdbldr_${det}_${dat}

  else

    /sbin/service incrond restart
    /sbin/service httpd restart

  fi

fi

fi

incrontab -u dbspool -d