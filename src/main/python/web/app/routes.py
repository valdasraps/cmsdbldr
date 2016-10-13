from config import HTTP_PASSWORD
from flask import Blueprint
from flask import Response
from flask import request
from functools import wraps
from loader import Loader

routes = Blueprint('routes', __name__)

def authenticate():
    """Sends a 401 response that enables basic auth"""

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth or auth.username is None or auth.password is None or auth.password != HTTP_PASSWORD:
            return Response(
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
    try:

        auth = request.authorization
        loader = Loader(det, db, auth)
        state = loader.load(request)

        status_code = 200
        if state != 0: 
            status_code = 500 + state
    
        resp = Response(loader.log, mimetype='text/plain')
        resp.status_code = status_code
        return resp

    except Exception as ex:
        
	print "ERROR:", ex

        resp = Response(str(ex), mimetype='text/plain')
        resp.status_code = 500

        if len(ex.args) == 2 and type(ex.args[0]) == int and type(ex.args[1]) in (str,unicode):
            resp = Response(ex.args[1], mimetype='text/plain')
            resp.status_code = ex.args[0]

        return resp
