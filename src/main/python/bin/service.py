#!/usr/bin/env python
from app import create_app

if __name__ == "__main__":
    create_app().run(host = app.config['HTTP_HOST'], port = app.config['HTTP_PORT'], debug = app.config['HTTP_DEBUG'])
