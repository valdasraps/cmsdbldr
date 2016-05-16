from app import app
from config import HTTP_ALLOWED_APPLICATIONS
from decorators import crossdomain
from flask import Response
from flask import jsonify
from flask import make_response
from flask import render_template
from flask import request
from functools import wraps
from loader import Loader
import subprocess

def check_auth(username, password):
    user_exists = subprocess.call(["id", "-u", username], stdout = subprocess.PIPE)
    return user_exists == 0 and password in HTTP_ALLOWED_APPLICATIONS

def authenticate():
    """Sends a 401 response that enables basic auth"""
    return Response(
    'Could not verify your access level for that URL.\n'
    'You have to login with proper credentials', 401,
    {'WWW-Authenticate': 'Basic realm="Login Required"'})

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or not check_auth(auth.username, auth.password):
            return authenticate()
        return f(*args, **kwargs)
    return decorated

@app.route('/<string:det>/<string:db>', methods=['POST'])
@requires_auth
def load(det, db):
    """
    Load file
    """
    
    loader = Loader(det, db)
    state = loader.load(request)

    status_code = 200
    if state != 0: 
        status_code = 500 + state
    
    resp = Response(loader.log, mimetype='text/plain')
    resp.status_code = status_code
    return resp
        
@app.route('/')
@crossdomain(origin='*')
def index():
    return render_template('index.html')

# --- error handlers ----


@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

@app.errorhandler(500)
def internal_error(error):
    return make_response(jsonify({'error': 'Internal server error'}), 500)
