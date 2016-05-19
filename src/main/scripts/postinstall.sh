#!/bin/sh

mkdir /opt/cmsdbldr/properties
chown -R dbspool /opt/cmsdbldr

mkdir -p /var/cmsdbldr/work
chown -R dbspool:dbspool /var/cmsdbldr

# Service stuff

cd $HOME
curl https://bootstrap.pypa.io/get-pip.py -o $HOME/get-pip.py
python $HOME/get-pip.py
pip install virtualenv virtualenvwrapper uwsgi honcho
rm -f $HOME/get-pip.py

cd /opt/cmsdbldr
virtualenv --system-site-packages venv
. venv/bin/activate
pip install -r bin/requirements.txt
