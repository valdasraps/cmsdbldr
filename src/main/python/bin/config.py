import os

BIN_DIR  = os.path.abspath(os.path.dirname(__file__))
BASE_DIR = os.path.split(BIN_DIR)[0]
PROP_DIR = os.path.join(BASE_DIR, 'properties')

DBSPOOL_BASE  = '/home/dbguru/dbspool'
DBSPOOL_BASE  = '/home/valdo/Downloads/dbloader/dbspool'

DBSPOOL_SPOOL = os.path.join(DBSPOOL_BASE, 'spool/')
DBSPOOL_STATE = os.path.join(DBSPOOL_BASE, 'state/')
DBSPOOL_LOGS  = os.path.join(DBSPOOL_BASE, 'logs/')

ALLOWED_EXTENSIONS = set(['xml', 'zip'])

# Runner

RUN_USAGE = 'usage: %prog [-v] [-u USER] FILE'
RUN_EXEC = os.path.join(BIN_DIR, 'cmsdbldr.sh')

RUN_WORK = '/var/cmsdbldr/work'

# Service

HTTP_DEBUG = False
HTTP_HOST = '0.0.0.0'
HTTP_PORT = 8181

HTTP_ALLOWED_APPLICATIONS = set(['http-service'])
HTTP_LOG = '/var/cmsdbldr/service.log'