#!/bin/sh

mkdir /opt/cmsdbldr/properties
chown -R dbspool /opt/cmsdbldr

mkdir -p /var/cmsdbldr/work
chown -R dbspool:dbspool /var/cmsdbldr