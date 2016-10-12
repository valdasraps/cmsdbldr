from config import HTTP_PASSWORD
from flask import Blueprint
from flask import Response
from flask import request
from functools import wraps
from loader import Loader
import subprocess

routes = Blueprint('routes', __name__)

def check_auth(username, password):
    user_exists = subprocess.call(["id", "-u", username], stdout = subprocess.PIPE)
    return user_exists == 0 and password == HTTP_PASSWORD

def authenticate():
    """Sends a 401 response that enables basic auth"""

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or auth.username is None or auth.password is None or auth.password != HTTP_PASSWORD:
            return Response(
                'Could not verify your access level for that URL.\n'
                'You have to login with proper credentials', 401,
                {'WWW-Authenticate': 'Basic realm="Login Required"'})
        return f(*args, **kwargs)
    return decorated

@routes.route('/')
def index():
    return 'CMS DB Loader'

@routes.route('/<string:det>/<string:db>', methods=['POST'])
@requires_auth
def load(det, db):
    """
    Load file
    """
    
    auth = request.authorization
    loader = Loader(det, db, auth)
    state = loader.load(request)

    status_code = 200
    if state != 0: 
        status_code = 500 + state
    
    resp = Response(loader.log, mimetype='text/plain')
    resp.status_code = status_code
    return resp
