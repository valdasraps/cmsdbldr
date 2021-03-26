#!/bin/sh
#
# service        Startup script for the CMS DB Loader service
#

cd "$(dirname "$0")"

det=$1
dat=$2
com=$3

name=cmsdbldr_${det}_${dat}
pidfile="/var/run/cmsdbldr_${det}_${dat}.pid"
properties="/opt/cmsdbldr/properties/${det}_${dat}.properties"
jarlib=/opt/cmsdbldr/bin/cmsdbldr.jar

workdir=/var/cmsdbldr/work/${det}/${dat}
outfile=/var/cmsdbldr/rest_${det}_${dat}.out.log
errfile=/var/cmsdbldr/rest_${det}_${dat}.err.log
jobslog=/var/cmsdbldr/jobs.log

do_start() {
    echo "$name $1"

    # Pre-create infra
    mkdir -p ${workdir}
    touch ${jobslog} ${outfile} ${errfile}
    chown -R dbspool:dbspool ${workdir} ${jobslog} ${outfile} ${errfile}

    /usr/bin/jsvc -server \
      -user dbspool \
      -pidfile ${pidfile} \
      -outfile ${outfile} \
      -errfile ${errfile} \
      -wait 30 \
      $1 \
      -cp $jarlib \
      -Doracle.net.tns_admin=/etc \
      -Xms512M -Xmx1024M \
      org.cern.cms.dbloader.rest.Application $properties
    RETVAL=$?
    return $RETVAL
}

do_doc() {
    TFOLDER=`mktemp -d`
    java -jar \
      -Doracle.net.tns_admin=/etc \
      -Xms512M -Xmx1024M \
      $jarlib \
      --properties $properties \
      --schema $TFOLDER >/dev/null
    mv $TFOLDER/schema1.xsd /opt/cmsdbldr/doc/${det}_${dat}.xsd
    xsltproc -o /opt/cmsdbldr/doc/${det}_${dat}.html /opt/cmsdbldr/doc/xs3p.xsl /opt/cmsdbldr/doc/${det}_${dat}.xsd
}

case "$com" in

    start)
        do_doc
        do_start
	RETVAL=$?
        ;;

    stop)
        do_start -stop
	RETVAL=$?
        ;;

    *)
        echo "Usage: $(dirname "$0")/$(basename "$0") {start|stop}"
        RETVAL=1
        ;;

esac
exit $RETVAL