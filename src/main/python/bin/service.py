#!/usr/bin/env python
from app import app

if __name__ == "__main__":
    app.run(host = app.config['HTTP_HOST'], port = app.config['HTTP_PORT'], debug = app.config['HTTP_DEBUG'])
