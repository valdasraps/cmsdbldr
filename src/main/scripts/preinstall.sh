#!/bin/sh

if [ `id -u dbspool 2>/dev/null || echo 0` -eq 0 ]; then 

    /usr/sbin/useradd -d /home/dbspool -c "cmsdbldr user" dbspool
    /usr/sbin/usermod -L dbspool
    mkdir -p /home/dbspool/spool /home/dbspool/state /home/dbspool/logs
    chmod 755 /home/dbspool

fi

