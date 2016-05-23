import sys
sys.path.insert(0, '/opt/cmsdbldr/bin')

from flask import Flask
from routes import routes
from config import HTTP_LOG

def create_app():
    app = Flask('CMS DB Loader')
    app.register_blueprint(routes)
    return app

def wsgi(*args, **kwargs):
    return create_app()(*args, **kwargs)

if __name__ == '__main__':
    create_app().run()

