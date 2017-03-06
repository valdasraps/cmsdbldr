#!/bin/sh

# Remove service

/sbin/service cmsdbldr stop
chkconfig --levels 345 cmsdbldr off

rm -f /etc/systemd/system/multi-user.target.wants/cmsdbldr.service

# Remove user incron

rm -fR /var/spool/incron/dbspool
incrontab -u dbspool -d

# Clean folders
rm -fR /opt/cmsdbldr/web/venv