import os

DEBUG = True
HOST = '0.0.0.0'
PORT = 8889

BASE_DIR = os.path.abspath(os.path.dirname(__file__))
LOG_DIR = os.path.join(BASE_DIR, 'logs/')

DBSPOOL_BASE = '/home/valdo/Downloads/dbspool'
DBSPOOL_DATA = os.path.join(DBSPOOL_BASE, 'data/')
DBSPOOL_STATE = os.path.join(DBSPOOL_BASE, 'state/')
DBSPOOL_LOGS = os.path.join(DBSPOOL_BASE, 'logs/')

ALLOWED_EXTENSIONS = set(['xml', 'zip'])
ALLOWED_APPLICATIONS = set(['gem-parts-application'])
