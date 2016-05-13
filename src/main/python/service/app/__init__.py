from flask import Flask

from subprocess import Popen
import os
import logging
from logging.handlers import RotatingFileHandler

app = Flask(__name__)
app.config.from_object('config')

# Set up logging
handler = RotatingFileHandler(
    os.path.join(app.config['LOG_DIR'], 'error.log'),
    maxBytes=100000,
    backupCount=1)
handler.setLevel(logging.ERROR)
handler.setFormatter(logging.Formatter("[ERROR] %(asctime)-15s \n%(message)s"))
app.logger.addHandler(handler)

from app import routes
