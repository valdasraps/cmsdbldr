from optparse import OptionParser
import requests
import sys
import os
import re
import importlib
from requests.auth import HTTPBasicAuth
import getpass

import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

USAGE = 'usage: %prog --url=URL FILE'

DEFAULT_USER = getpass.getuser()
SSO_COOKIE_PROVIDER = "cern_sso_api:cern_sso_cookies"
SSO_LOGIN_URL = "https://login.cern.ch/"

ALLOWED_EXTENSIONS = ['xml','zip']

class LoaderClient:
    
    def __init__(self):
        
        self.parser = OptionParser(USAGE)
        self.parser.add_option("-u", "--url",   dest = "url",   help = "service URL", metavar = "url")
        self.parser.add_option("-o", "--sso",   dest = "sso",   help = "use cookie provider from cern_sso_api module", metavar = "sso", action = "store_true", default=False)
        self.parser.add_option("-q", "--quiet", dest = "quiet", help = "Do not print error, just return its code. OK = 0", action = "store_true", default = False)

    def _allowed_file(self, filename):
        return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS

    def run(self):

        try:

            (options, args) = self.parser.parse_args()

            if options.url is None:
                self.parser.error('Please provide service URL')

            url = options.url
            while url.endswith('/'): url = url[:-1]
            if not re.search("/load/file$", url):
                url = url + "/load/file"

            if len(args) != 1:
                self.parser.error('Please provide one data file?')
                return 1

            cprov = None
            if options.sso and re.search("^https", url):
                mod_name, fun_name = SSO_COOKIE_PROVIDER.split(":")
                m = importlib.import_module(mod_name)
                cprov = getattr(m, fun_name)
            
            f = args[0]
            if not (os.path.isfile(f) and os.path.exists(f)):
                self.parser.error('File [%s] not found or is not a file?' % f)
                return 2
            if not self._allowed_file(f):
                self.parser.error('File [%s] allowed? Allowed extensions are (%s)' % (f, ','.join(ALLOWED_EXTENSIONS)))
                return 2

            files = {'uploadFile': open(f, 'rb')}
            force_level = 0
            while True:

                cookies = None
                if cprov is not None:
                    cookies = cprov(url, force_level = force_level)

                r = requests.post(url = url, files = files, cookies = cookies, verify = False)

                if r.status_code == 200 and r.url.startswith(SSO_LOGIN_URL):
                    if force_level < 2:
                        force_level = force_level + 1
                        continue
                    else:
                        if not options.sso:
                            raise Exception('Resource is secured by SSO. Please try --sso')
                        else:
                            raise Exception('Error while logging to HTTPS/SSO')

                if not options.quiet:
                    print(r.text)
                    print("Response code: %d" % r.status_code)

                if r.status_code == requests.codes.ok:
                    return 0

                else:
                    return int(str(r.status_code)[0])

        except Exception as e:
            
            import traceback
            traceback.print_exc()

            print("ERROR: %s\nDetails: %s" % (type(e).__name__, e))

if __name__ == '__main__':

    cli = LoaderClient()
    sys.exit(cli.run())
