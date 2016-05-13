import os
import time
from werkzeug import secure_filename
from config import *

def _check_dir(dir):
    return (os.path.isdir(dir) and os.path.exists(dir))
    
def _allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS
    
class Loader:
    
    def __init__(self, det, db):
    
        # Checking directories
        self.data_dir = os.path.join(DBSPOOL_DATA, det + "/" + db)
        if not _check_dir(self.data_dir): abort(404)
        self.state_dir = os.path.join(DBSPOOL_STATE, det + "/" + db)
        if not _check_dir(self.state_dir): abort(500)
        self.logs_dir = os.path.join(DBSPOOL_LOGS, det + "/" + db)
        if not _check_dir(self.logs_dir): abort(500)
    
    def load(self, request):
        
        # Checking file
        file = request.files['file']
        if file is None: abort(400)
        if not _allowed_file(file.filename): abort(406)
        
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