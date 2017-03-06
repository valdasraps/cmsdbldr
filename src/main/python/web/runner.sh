#!/bin/sh

cd "$(dirname "$0")"

. venv/bin/activate
uwsgi --ini /opt/cmsdbldr/web/uwsgi.ini
