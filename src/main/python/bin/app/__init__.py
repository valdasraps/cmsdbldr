from flask import Flask

from subprocess import Popen
import os
import logging
from logging.handlers import RotatingFileHandler

def create_app():
    app = Flask(__name__)
    app.config.from_object('config')

    # Set up logging
    handler = RotatingFileHandler(
        app.config['HTTP_LOG'],
        maxBytes=100000,
        backupCount=1)
    handler.setLevel(logging.INFO)
    handler.setFormatter(logging.Formatter("[ERROR] %(asctime)-15s \n%(message)s"))
    app.logger.addHandler(handler)

    from app import routes
    return app

def wsgi(*args, **kwargs):
    return create_app()(*args, **kwargs)

if __name__ == '__main__':
    create_app().run()