#!/bin/sh

chown dbspool:dbspool /var/cmsdbldr

# Install python stuff

cd $HOME
python /opt/cmsdbldr/ext/get-pip.py --no-index --find-links file:///opt/cmsdbldr/ext --no-wheel
pip install --no-index --find-links file:///opt/cmsdbldr/ext virtualenv virtualenvwrapper uwsgi honcho

# Setup venv

cd /opt/cmsdbldr/web
virtualenv --system-site-packages --no-download venv
. venv/bin/activate
pip install --no-index --find-links file:///opt/cmsdbldr/ext -r requirements.txt
chown -R dbspool:dbspool /opt/cmsdbldr/web/venv

# Install service

chkconfig --levels 345 cmsdbldr on

# Restart services

/sbin/service incrond restart
/sbin/service httpd restart
/sbin/service cmsdbldr start

# Fix firewall
/sbin/iptables -L -n | grep "dpt:80 "
if [ $? -ne 0 ]; then
  iptables -I INPUT -i eth0 -p tcp --dport 80 -m state --state NEW,ESTABLISHED -j ACCEPT
  /sbin/service iptables save
  setsebool httpd_can_network_connect 1
fi