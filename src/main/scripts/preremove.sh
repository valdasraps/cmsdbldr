#!/bin/sh

# Remove service

/sbin/service cmsdbldr stop
chkconfig --levels 345 cmsdbldr off

# Remove user incron

rm -fR /var/spool/incron/dbspool
incrontab -u dbspool -d

# Clean folders
rm -fR /opt/cmsdbldr/web/venv