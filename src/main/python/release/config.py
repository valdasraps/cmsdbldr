import os

USAGE = 'usage: %prog [-v] [-u USER] FILE'

BASE_DIR = os.path.abspath(os.path.dirname(__file__))
EXEC = os.path.join(BASE_DIR, 'cmsdbldr.sh')
PROP_DIR = os.path.join(BASE_DIR, 'properties')

JAVA_HOME = '/home/dbguru/app/jdk1.8.0_66'
JAVA_HOME = '/usr/lib/jvm/java-8-oracle'

JAVA_APP = os.path.join(JAVA_HOME, 'bin/java')
TNS_ADMIN = '/etc'

SPOOL_BASE = '/home/dbguru/dbspool'
SPOOL_BASE = '/home/valdo/Downloads/dbloader/dbspool'

SPOOL_SPOOL = os.path.join(SPOOL_BASE, 'spool/')
SPOOL_STATE = os.path.join(SPOOL_BASE, 'state/')
SPOOL_LOGS = os.path.join(SPOOL_BASE, 'logs/')

LOGS_BASE = '/home/dbguru/logs'
LOGS_BASE = '/home/valdo/Downloads/dbloader/dbguru/logs'

ALLOWED_EXTENSIONS = set(['xml', 'zip'])
