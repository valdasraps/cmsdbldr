
from optparse import OptionParser
import requests
import sys
import os
import re
import logging
import tempfile
import subprocess
import json
import warnings
from base64 import b64encode, b64decode
import xml.etree.ElementTree as ET

import urllib3
from urllib.parse import urlparse

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

if sys.version_info < (3,):
    from cookielib import MozillaCookieJar
else:
    from http.cookiejar import MozillaCookieJar


class CernSSO:

    DEFAULT_TIMEOUT_SECONDS = 10

    def load_cookies_from_mozilla(self, filename):
        ns_cookiejar = MozillaCookieJar()
        ns_cookiejar.load(filename, ignore_discard=True, ignore_expires=True)
        return ns_cookiejar

    def krb_sign_on(self, url, cookiejar={}, force_level = 0):

        tfile = tempfile.mktemp()
        cmd = 'auth-get-sso-cookie -u "%s" -o "%s" -v --nocertverify' % (url,tfile)
        with warnings.catch_warnings():
            p = subprocess.Popen(cmd, stdout = subprocess.PIPE, stderr = subprocess.PIPE, shell = True)
        logging.debug("%s returned %s" % (cmd, p.returncode))
        logging.debug(p.stdout.read())
        err = p.stderr.read()
        logging.debug(err)
        cookies = self.load_cookies_from_mozilla(tfile)
        os.remove(tfile)
        return cookies

    def file_mtime(self, file_path):
        try:
            return os.path.getmtime(file_path)
        except OSError:
            return -1

    def html_root(self, r):
        c = r.content.decode('utf-8')
        c = re.sub('<meta [^>]*>','', c, flags = re.IGNORECASE)
        c = re.sub('<hr>','', c, flags = re.IGNORECASE)
        c = re.sub("=\\'([^']*)\\'",'="\g<1>"', c, flags = re.IGNORECASE)
        c = re.sub(" autofocus "," ", c, flags = re.IGNORECASE)
        c = re.sub('<img ([^>]*)>','<img \g<1>/>',c, flags = re.IGNORECASE)
        c = re.sub('<script>[^<]*</script>','',c, flags = re.IGNORECASE)
        return ET.fromstring(c)

    def read_form(self, r):
        root = self.html_root(r)
        form = root.find(".//{http://www.w3.org/1999/xhtml}form")
        action = form.get('action')
        form_data = dict(((e.get('name'), e.get('value')) for e in form.findall(".//{http://www.w3.org/1999/xhtml}input")))
        return action, form_data

    def is_email(self, s):
        regex = '^[a-z0-9]+[\._]?[a-z0-9]+[@]\w+[.]\w{2,3}$'
        return re.search(regex, s)

    def split_url(self, url):
        a = len(urlparse(url).query) + len(urlparse(url).path)
        return url[:-(a + 1)], url[len(url) - a - 1:]

    def login_sign_on(self, url, cache_file = ".session.cache", force_level = 0):

        from ilock import ILock
        from getpass import getpass
        from os import remove, path

        cache = None
        cache_file = path.abspath(cache_file)
        cache_lock_id = b64encode(cache_file.encode('utf-8')).decode()
        cache_time = self.file_mtime(cache_file)

        with ILock(cache_lock_id):

            if force_level == 1 and cache_time != self.file_mtime(cache_file):
                force_level = 0

            if force_level == 2:
                remove(cache_file)

            if path.isfile(cache_file):

                logging.debug('%s found', cache_file)
                with open(cache_file, 'r') as f:
                    cache = json.loads(f.read())

            else:

                logging.debug('%s not found', cache_file)

            if force_level > 0 or cache is None or 'cookies' not in cache:

                if cache is not None and 'secret' in cache:
                    secret = b64decode(cache['secret'].encode()).decode()
                    username, password = secret.split('/')
                    password = b64decode(password.encode()).decode()
                else:
                    username = input("Username: ")
                    password = getpass("Password: ")
                    logging.warning('Credentials will be stored in a NOT secure way!')

                with requests.Session() as s:

                    r1 = s.get(url, timeout = 10, verify = False, allow_redirects = True)
                    r1.raise_for_status()

                    if self.is_email(username):
                        logging.debug("%s is guest account." % username)

                        root = self.html_root(r1)
                        link = root.find(".//{http://www.w3.org/1999/xhtml}a[@id='zocial-guest']")
                        guest_url = self.split_url(r1.url)[0] + self.split_url(link.get('href'))[1]
                        logging.debug(guest_url)
                        r1 = s.get(guest_url, timeout = 10, verify = False, allow_redirects = True)
                        r1.raise_for_status()

                    else:
                        logging.debug("%s is a regular account." % username)

                    action, form_data = self.read_form(r1)

                    form_data['username'] = username
                    form_data['password'] = password

                    r2 = s.post(url = action, data = form_data, timeout = self.DEFAULT_TIMEOUT_SECONDS, allow_redirects = True)
                    r2.raise_for_status()
                    action, form_data = self.read_form(r2)

                    r3 = s.post(url = action, data = form_data, timeout = self.DEFAULT_TIMEOUT_SECONDS, allow_redirects = True)
                
                    cache = {
                        'secret': b64encode((username + '/' + b64encode(password.encode()).decode()).encode()).decode(),
                        'location': url,
                        'cookies': { c.name: c.value for c in s.cookies }
                    }

                    with open(cache_file, 'w') as f:
                        f.write(json.dumps(cache))

            return cache['cookies']


class LoaderClient:

    USAGE = 'usage: %prog --url=URL FILE'
    ALLOWED_EXTENSIONS = ['xml','zip']
    
    def __init__(self):
        
        self.parser = OptionParser(self.USAGE)
        self.parser.add_option("-u", "--url",      dest = "url",      help = "service URL", metavar = "url")
        self.parser.add_option("-o", "--login",    dest = "login",    help = "use simple login provider cache (stores pwd in not secure way!)", metavar = "login", action = "store_true", default = False)
        self.parser.add_option("-k", "--krb",      dest = "krb",      help = "use kerberos login provider", metavar = "krb", action = "store_true", default = False)
        self.parser.add_option("-q", "--quiet",    dest = "quiet",    help = "Do not print error, just return its code. OK = 0", action = "store_true", default = False)
        self.parser.add_option("-v", "--verbose",  dest = "verbose",  help = "Print debug information (verbose output). Be carefull: this might expose password to terminal!", action = "store_true", default = False)
        self.parser.add_option("-a", "--validate", dest = "validate", help = "EXPERIMENTAL! Do not send XML file to loader but validate XML instead!", action = "store_true", default = False)
        self.parser.add_option("-t", "--test",     dest = "test",     help = "Upload test - proceed with the full upload process but rollback the transaction!", action = "store_true", default = False)
        self.parser.add_option("-c", "--loginc",   dest = "loginc",   help = "Specify cache file", metavar = "loginc", default = ".session.cache")

    def _allowed_file(self, filename):
        return '.' in filename and filename.rsplit('.', 1)[1] in self.ALLOWED_EXTENSIONS

    def _validate_xml(self, xml, xsd, quiet = False):
        cmd = 'xmllint --schema "%s" --noout "%s"' % (xsd,xml)
        p = subprocess.Popen(cmd, stdout = subprocess.PIPE, stderr = subprocess.PIPE, shell = True)
        logging.debug("%s returned %s" % (cmd, p.returncode))
        out = p.stdout.read()
        logging.debug(out)
        err = p.stderr.read()
        logging.debug(err)
        if not quiet and p.returncode != 0:
            print(err.decode("utf-8"))
        return p.returncode

    def run(self, iargs = []):

        SSO_LOGIN_URL = "https://auth.cern.ch/"

        try:

            if len(iargs) != 0:
                (options, args) = self.parser.parse_args(iargs)
            else:
                (options, args) = self.parser.parse_args()

            logging_level = logging.DEBUG if options.verbose else logging.WARN
            logging.basicConfig(level = logging_level)

            if options.url is None:
                self.parser.error('Please provide service URL')

            url = options.url
            while url.endswith('/'): url = url[:-1]
            if re.search("/load/file$", url):
                self.parser.error('Please provide service URL without /load/file section')
                return 1

            if re.search("^https", url):
                if sum((options.login, options.krb)) != 1:
                    self.parser.error('For secure access please provide one of --krb or --login')
                    return 1

            load_url = url + "/load/file"
            if options.test:
                load_url = load_url + "?test"
            logging.debug("load_url = %s", load_url)

            if len(args) != 1:
                self.parser.error('Please provide one data file?')
                return 1

            filename = args[0]
            if not (os.path.isfile(filename) and os.path.exists(filename)):
                self.parser.error('File [%s] not found or is not a file?' % filename)
                return 2

            if not self._allowed_file(filename):
                self.parser.error('File [%s] allowed? Allowed extensions are (%s)' % (filename, ','.join(self.ALLOWED_EXTENSIONS)))
                return 2

            force_level = 0
            while True:

                cookies = None
                if re.search("^https", url):

                    if options.login:
                        cookies = CernSSO().login_sign_on(url, cache_file = options.loginc, force_level = force_level)

                    elif options.krb:
                        cookies = CernSSO().krb_sign_on(url)
                        force_level = 2

                if options.validate:
                    if filename.rsplit('.', 1)[1].lower() != 'xml':
                        self.parser.error('Validation is possible for xml files only')
                        return 2
                    else:
                        r = requests.get(url = url + "/doc/xsd", cookies = cookies, verify = False)
                        temp_file = tempfile.mktemp(suffix='.xsd')
                        with open(temp_file, 'w') as fp:
                            fp.write(r.text)
                        return self._validate_xml(filename, temp_file, options.quiet)

                else:

                    with open(filename, 'rb') as fp:
                        r = requests.post(url = load_url, files = {'uploadFile': fp}, cookies = cookies, verify = False)

                if r.status_code == 200 and r.url.startswith(SSO_LOGIN_URL):
                    if force_level < 2:
                        force_level = force_level + 1
                        continue
                    else:
                        if not options.login and not options.krb:
                            raise Exception('Resource is secured by SSO. Please try --krb or --login')
                        else:
                            raise Exception('Error while logging to HTTPS/SSO')

                if not options.quiet:
                    print(r.text)
                    print("Response code: %d" % r.status_code)
                    if options.test:
                        print("This was a test run, no changes in DB commited!")

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

