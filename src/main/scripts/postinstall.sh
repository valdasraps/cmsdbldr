#!/bin/sh

grep --quiet CentOS /etc/system-release
CENTOS=$?

chown dbspool:dbspool /var/cmsdbldr

# Compile and copy newer jsvc

JAVA_HOME=`java -XshowSettings:properties -version 2>&1 > /dev/null | grep 'java.home' | sed 's/^.*= *//g'`

cd /opt/cmsdbldr/ext/
tar xvfz commons-daemon-*-src.tar.gz 
cd commons-daemon-*-src/src/native/unix/
./configure --with-java=${JAVA_HOME}
make
cp jsvc /opt/cmsdbldr/bin
cd /opt/cmsdbldr/ext/
rm -fR commons-daemon-*-src

# Install service and restart

if [ "$CENTOS" == 0 ]; then

  systemctl daemon-reload
  systemctl restart incrond
  systemctl restart httpd

else

  /sbin/service incrond restart
  /sbin/service httpd restart

fi

# Fix firewall
/sbin/iptables -L -n | grep "dpt:80 "
if [ $? -ne 0 ]; then
  iptables -I INPUT -i eth0 -p tcp --dport 80 -m state --state NEW,ESTABLISHED -j ACCEPT
  /sbin/service iptables save
  if sestatus | grep enabled; then
    setsebool httpd_can_network_connect 1
  fi
fi
