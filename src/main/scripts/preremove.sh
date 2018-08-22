#!/bin/sh

# Remove user incron

rm -fR /var/spool/incron/dbspool
incrontab -u dbspool -d
