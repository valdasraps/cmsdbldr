#!/bin/sh

# Adding user (if not exists)
if [ `id -u dbspool 2>/dev/null || echo 0` -eq 0 ]; then 

    /usr/sbin/useradd -d /home/dbspool -c "cmsdbldr user" dbspool
    /usr/sbin/usermod -L dbspool

fi

mkdir -p /home/dbspool/{spool,state,logs}
chown -R dbspool:dbspool /home/dbspool/{spool,state,logs}
chmod 755 /home/dbspool