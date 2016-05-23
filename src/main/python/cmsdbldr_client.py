from optparse import OptionParser
import requests
import sys
import os
from requests.auth import HTTPBasicAuth
import getpass

USAGE = 'usage: %prog [-h HOST] [-p PORT] [-u USER] -k KEY -d DET -b DAT FILE [FILE]'

DEFAULT_HOST = "localhost"
DEFAULT_PORT = 80
DEFAULT_USER = getpass.getuser()
DEFAULT_KEY = 'somekey'

ALLOWED_EXTENSIONS = ['xml','zip']

class CLIClient:
    
    def __init__(self):
        
        self.parser = OptionParser(USAGE)
        self.parser.add_option("-H", "--host",     dest = "host",     help = "service HOST (default: %s)" % DEFAULT_HOST, metavar = "HOST", default = DEFAULT_HOST)
        self.parser.add_option("-p", "--port",     dest = "port",     help = "service PORT (default: %s)" % DEFAULT_PORT, metavar = "PORT", default = DEFAULT_PORT, type="int")
        self.parser.add_option("-u", "--user",     dest = "user",     help = "uploading USER (default: %s)" % DEFAULT_USER, metavar = "USER", default = DEFAULT_USER)
        self.parser.add_option("-k", "--key",      dest = "key",      help = "service KEY", metavar = "KEY")
        self.parser.add_option("-d", "--det",      dest = "det",      help = "DETECTOR indicator", metavar = "DET")
        self.parser.add_option("-b", "--dat",      dest = "dat",      help = "DATABASE indicator", metavar = "DAT")
        self.parser.add_option("-q", "--quiet",    dest = "quiet",    help = "Do not print error, just return its code. OK = 0", action = "store_true", default = False)

    def _allowed_file(self, filename):
        return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

    def run(self):

        try:

            (options, args) = self.parser.parse_args()

            if options.key is None:
                self.parser.error('Please provide service key')

            if options.det is None:
                self.parser.error('Please provide detector indicator')

            if options.dat is None:
                self.parser.error('Please provide destination database indicator')

            if len(args) == 0:
                self.parser.error('Please provide at least one data file?')
                return 1
            
            files = []
            for f in args:
                if not (os.path.isfile(f) and os.path.exists(f)):
                    self.parser.error('File [%s] not found or is not a file?' % f)
                    return 2
                if not self._allowed_file(f):
                    self.parser.error('File [%s] allowed? Allowed extensions are (%s)' % (f, ','.join(ALLOWED_EXTENSIONS)))
                    return 2
                files.append(f)

            url='/'.join(('http:/', options.host + ':' + str(options.port), 'cmsdbldr', options.det, options.dat))
            for f in files:
                files = {'file': open(f, 'rb')}
                r = requests.post(url, files=files, auth=HTTPBasicAuth(options.user, options.key))
                if not options.quiet:
                    print r.text
                    print "Response code: %d" % r.status_code
                if r.status_code == requests.codes.ok:
                    return 0
                else:
                    return int(str(r.status_code)[0])

        except Exception, e:
            
            print "ERROR: %s\nDetails: %s" % (type(e).__name__, e)

if __name__ == '__main__':

    cli = CLIClient()
    sys.exit(cli.run())