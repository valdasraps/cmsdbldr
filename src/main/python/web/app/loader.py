import os
import time
from werkzeug import secure_filename
from config import *
from egroup import EGroup
import subprocess

def _check_dir(dir):
    return (os.path.isdir(dir) and os.path.exists(dir))

def _check_file(file):
    return (os.path.isfile(file) and os.path.exists(file))
    
def _allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS
    
def _load_properties(filepath, sep = '=', comment_char = '#'):
    """
    Read the file passed as parameter as a properties file.
    """
    props = {}
    with open(filepath, "rt") as f:
        for line in f:
            l = line.strip()
            if l and not l.startswith(comment_char):
                key_value = l.split(sep)
                key = key_value[0].strip()
                value = sep.join(key_value[1:]).strip().strip('"') 
                props[key] = value 
    return props
    
def _bool(str):
    return str.lower() in ("yes", "true", "t", "1")

def _check_key(d, v):
    return v in d and d[v] is not None
    
class Loader:
    
    def __init__(self, det, db, auth):
    
        # Checking directories
        self.data_dir = os.path.join(DBSPOOL_SPOOL, det + "/" + db)
        if not _check_dir(self.data_dir): raise Exception(404, "Detector (%s) and database (%s) not supported?" % (det, db))
        self.state_dir = os.path.join(DBSPOOL_STATE, det + "/" + db)
        if not _check_dir(self.state_dir): raise Exception(500, "Server configuration issue (state directory not found)?")
        self.logs_dir = os.path.join(DBSPOOL_LOGS, det + "/" + db)
        if not _check_dir(self.logs_dir): raise Exception(500, "Server configuration issue (logs directory not found)?")
        
        # Authentication
        props_file = os.path.join(PROP_DIR, det + '_' + db + '.properties')
        if not _check_file(props_file): raise Exception(500, "Server configuration error (properties file not found)?")
        self.authenticate(_load_properties(props_file), auth)
    
    def authenticate(self, props, auth):
        
        if 'user_check_egroup' in props and props['user_check_egroup'] is not None:
            
            if not 'user_check_egroup_username' in props and props['user_check_egroup_username'] is not None: 
                raise Exception(500, "Server configuration issue (no user_check_egroup_username defined)?")
            if not 'user_check_egroup_password' in props and props['user_check_egroup_password'] is not None:
                raise Exception(500, "Server configuration issue (no user_check_egroup_password defined)?")
            
            members = EGroup(props['user_check_egroup_username'], props['user_check_egroup_password']).members(props['user_check_egroup'])
            if not auth.username.upper() in map(str.upper, members):
                raise Exception(401, "User (%s) not authorized by egroup (%s)?" % (auth.username, props['user_check_egroup']))
        
        if 'user_check_os' in props and _bool(props['user_check_os']):
            if 0 != subprocess.call(["id", "-u", auth.username], stdout = subprocess.PIPE):
                raise Exception(401, "User (%s) not authorized by os?" % auth.username)
    
    def load(self, request):
        
        # Checking file
        file = request.files['file']
        if file is None: raise Exception(400, "File not provided?")
        if not _allowed_file(file.filename): raise Exception(406, "File type not allowed?")
        
        # Setup file names
        filename = secure_filename(file.filename)
        data_file = os.path.join(self.data_dir, filename)
        state_file = os.path.join(self.state_dir, filename)
        logs_file = os.path.join(self.logs_dir, filename)

        # Remove state file (if exists)
        if os.path.exists(state_file):
            os.remove(state_file)

        # Copy data file
        file.save(data_file)

        # Wait for state file to appear
        while not os.path.exists(state_file):
            time.sleep(1)
        
        # Read log
        self.log = open(logs_file, 'r').read()
        
        # Return status
        return int(open(state_file, 'r').readline())
