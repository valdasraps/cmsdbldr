#!/bin/sh

chown dbspool:dbspool /var/cmsdbldr

# Install python stuff

cd $HOME
curl https://bootstrap.pypa.io/get-pip.py -o $HOME/get-pip.py
python $HOME/get-pip.py
pip install virtualenv virtualenvwrapper uwsgi honcho
rm -f $HOME/get-pip.py

# Setup venv

cd /opt/cmsdbldr/web
virtualenv --system-site-packages venv
. venv/bin/activate
pip install -r requirements.txt
chown -R dbspool:dbspool /opt/cmsdbldr/web/venv

# Install service

chkconfig --levels 345 cmsdbldr on

# Restart services

/sbin/service incrond restart
/sbin/service httpd restart
/sbin/service cmsdbldr start