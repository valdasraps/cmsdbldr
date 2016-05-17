#!/bin/sh

[ "$(ls -A /opt/cmsdbldr/properties)" ] || rm -fR /opt/cmsdbldr
/usr/sbin/userdel -rf dbspool