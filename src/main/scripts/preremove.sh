#!/bin/sh

# Remove built app and doc

rm -f  /opt/cmsdbldr/bin/jsvc
rm -fR /opt/cmsdbldr/doc

# Remove user incron

rm -fR /var/spool/incron/dbspool
incrontab -u dbspool -d
