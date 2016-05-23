#!/bin/sh

# Force removal of var

rm -fR /var/cmsdbldr

# Remove user

/usr/sbin/userdel -rf dbspool