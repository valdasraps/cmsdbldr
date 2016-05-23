import os

BIN_DIR  = os.path.abspath(os.path.dirname(__file__))
BASE_DIR = os.path.split(BIN_DIR)[0]
PROP_DIR = os.path.join(BASE_DIR, 'properties')

DBSPOOL_BASE  = '/home/dbspool'
DBSPOOL_SPOOL = os.path.join(DBSPOOL_BASE, 'spool/')
DBSPOOL_STATE = os.path.join(DBSPOOL_BASE, 'state/')
DBSPOOL_LOGS  = os.path.join(DBSPOOL_BASE, 'logs/')

ALLOWED_EXTENSIONS = set(['xml', 'zip'])

# Runner

RUN_USAGE = 'usage: %prog [-v] [-u USER] FILE'
RUN_EXEC = os.path.join(BIN_DIR, 'cmsdbldr.sh')

RUN_WORK = '/var/cmsdbldr/work'

HTTP_PASSWORD = 'kucr3PREruVUchAwEc'