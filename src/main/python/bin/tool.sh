#!/bin/bash

cd "$(dirname "$0")"

aopt=0
ropt=0

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

DBSPOOL=/home/dbspool
DETDAT=$det/$dat
PROPS=/opt/cmsdbldr/properties/${det}_${dat}.properties

rm -fR $DBSPOOL/spool/$DETDAT $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
tf=`mktemp`
cat /var/spool/incron/dbspool | grep -v "$DBSPOOL/spool/$DETDAT/" >$tf
mv $tf /var/spool/incron/dbspool

if [[ 1 -eq $aopt ]]; then

  mkdir -p $DBSPOOL/spool/$DETDAT $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
  chown -R dbspool:dbspool $DBSPOOL/spool/$det $DBSPOOL/state/$det $DBSPOOL/logs/$det
  chmod 755 $DBSPOOL/spool/$det $DBSPOOL/state/$det $DBSPOOL/logs/$det
  chmod 755 $DBSPOOL/state/$DETDAT $DBSPOOL/logs/$DETDAT
  chmod 777 $DBSPOOL/spool/$DETDAT

  touch $PROPS
  chown dbspool:dbspool $PROPS

  echo "$DBSPOOL/spool/$DETDAT IN_CREATE,IN_MOVED_TO /opt/cmsdbldr/bin/runner.sh $DBSPOOL/spool/$DETDAT/\$#" >>/var/spool/incron/dbspool

fi

incrontab -u dbspool -d
