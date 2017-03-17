import sys
import os
import shutil
from pwd import getpwuid
from optparse import OptionParser
from tempfile import mkdtemp
from config import *
import subprocess
from shutil import copyfile
from datetime import datetime

def _allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

class Loader:
    
    def __init__(self):
    
        self.parser = OptionParser(RUN_USAGE)
        self.parser.add_option("-v", "--verbose",  dest = "verbose",  help = "verbose output", action = "store_true", default = False)
        self.parser.add_option("-u", "--user",     dest = "user",     help = "file USER", metavar = "USER")

    def vprint(self, line, *args):
        if self.verbose:
            print line % (args)
            
    def error(self, code, line, *args):
        self.parser.error(line % (args))
        return code

    def run(self):

        (options, args) = self.parser.parse_args()
        self.verbose = options.verbose

        # Checking properties

        if len(args) == 0:
            return self.error(1, 'File to upload not provided')
        
        if len(args) > 1:
            return self.error(1, 'More than one file provided')
        
        # Checking files
        
        file = os.path.abspath(args[0])

        self.vprint("File: %s", file)
        
        if not _allowed_file(file):
            return self.error(2, 'File %s type not allowed? Must be one of [%s]', file, ','.join(ALLOWED_EXTENSIONS))
        
        if not file.startswith(DBSPOOL_SPOOL):
            return self.error(2, '%s not in spool folder %s?', file, DBSPOOL_SPOOL)
        
        if not (os.path.exists(file) and os.path.isfile(file)):
            return self.error(2, '%s not found or is not a file?', file)
        
        user = options.user
        if user is None:
            user = getpwuid(os.stat(file).st_uid).pw_name
        self.vprint("File user: %s", user)
        
        (file_folder, filename) = os.path.split(file)
        self.vprint("Filename: %s", filename)
        
        file_folder = file_folder.replace(DBSPOOL_SPOOL, '')
        
        (detector, database) = os.path.split(file_folder)
        self.vprint("Detector: %s", detector)
        self.vprint("Database: %s", database)
        
        if detector is None or database is None:
            return self.error(2, 'Wrong %s location? Detector and/or database not determined.', file)

        prop = os.path.join(PROP_DIR, "%s_%s.properties" % (detector, database))
        self.vprint("Properties: %s", prop)
        if not (os.path.exists(prop) and os.path.isfile(prop)):
            return self.error(2, '%s not found or is not a file?', prop)

        # Creating temporary folder

        logs = os.path.join(RUN_WORK, detector, database)
        if not os.path.exists(logs): 
            os.makedirs(logs)
            
        work = mkdtemp(prefix = filename + '-', dir = logs)
        self.vprint("Work: %s", work)
        
        with open(JOBS_LOG,'r+') as f:
            f.seek(0, 2)
            f.write("%s\t%s\n" % (unicode(datetime.now()), work))

        log = os.path.join(work, "output.log")
        work = os.path.join(work, filename)
        
        # Preparing response
        
        state = os.path.join(DBSPOOL_STATE, detector, database)
        if not os.path.exists(state): os.makedirs(state)
        state = os.path.join(state, filename)
        os.remove(state) if os.path.exists(state) else None
        self.vprint("State: %s", state)
        
        logs = os.path.join(DBSPOOL_LOGS, detector, database)
        if not os.path.exists(logs): os.makedirs(logs)
        logs = os.path.join(logs, filename)
        os.remove(logs) if os.path.exists(logs) else None
        self.vprint("Logs: %s", logs)
        
        # Move the file and execute loader
        
        shutil.move(file, work)
        
        args = [ 
            RUN_EXEC, 
            '--properties=%s' % prop,
            '--file-user=%s' % user,
            work
        ]
        
        status = 3
        with open(log, "w") as f:
            
            status = subprocess.call(args, stdout = f, stderr = subprocess.STDOUT)

        copyfile(log, logs)
        
        with open(state, "w") as f:
            f.write(str(status))
        
        return status

if __name__ == '__main__':

    load = Loader()
    sys.exit(load.run())
