#!/bin/sh

cd $(dirname "$0")

if [ ! -d venv ]; then
  virtualenv --system-site-packages venv
  . venv/bin/activate
  pip install -r requirements.txt 
else
  . venv/bin/activate
fi
python ./service.py
