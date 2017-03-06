#!/bin/sh

grep --quiet CentOS /etc/system-release
CENTOS=$?

chown dbspool:dbspool /var/cmsdbldr

# Install python stuff (SLC only)
if [ "$CENTOS" != 0 ]; then

  cd $HOME
  python /opt/cmsdbldr/ext/get-pip.py --no-index --find-links file:///opt/cmsdbldr/ext --no-wheel
  pip install --no-index --find-links file:///opt/cmsdbldr/ext virtualenv virtualenvwrapper uwsgi honcho suds

fi

# Setup venv
cd /opt/cmsdbldr/web

if [ "$CENTOS" == 0 ]; then

  virtualenv --system-site-packages venv

else

  virtualenv --system-site-packages --no-download venv

fi

. venv/bin/activate

pip install --no-index --find-links file:///opt/cmsdbldr/ext -r requirements.txt --upgrade
chown -R dbspool:dbspool /opt/cmsdbldr/web/venv

# Install service and restart

if [ "$CENTOS" == 0 ]; then

  systemctl daemon-reload
  systemctl enable cmsdbldr

  systemctl restart incrond
  systemctl restart httpd
  systemctl start cmsdbldr

else

  chkconfig --levels 345 cmsdbldr on

  /sbin/service incrond restart
  /sbin/service httpd restart
  /sbin/service cmsdbldr start

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
