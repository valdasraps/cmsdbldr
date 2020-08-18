# Copyright (C) 2017, CERN
# This software is distributed under the terms of the GNU General Public
# Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
# In applying this license, CERN does not waive the privileges and immunities
# granted to it by virtue of its status as Intergovernmental Organization
# or submit itself to any jurisdiction.

from optparse import OptionParser
import requests
import sys
import os
import re
import importlib
from requests.auth import HTTPBasicAuth
import getpass
import logging

import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

class CernSSO:

    DEFAULT_TIMEOUT_SECONDS = 10

    def _init_session(self, s, url, cookiejar, auth_url_fragment):
        """
        Internal helper function: initialise the sesion by trying to access
        a given URL, setting up cookies etc.

        :param: auth_url_fragment: a URL fragment which will be joined to
        the base URL after the redirect, before the parameters. Examples are
        auth/integrated/ (kerberos) and auth/sslclient/ (SSL)
        """

        from six.moves.urllib.parse import urlparse, urljoin

        # Try getting the URL we really want, and get redirected to SSO
        logging.debug("Fetching URL: %s" % url)
        r1 = s.get(url, timeout=self.DEFAULT_TIMEOUT_SECONDS, verify = False, cookies = cookiejar)

        # Parse out the session keys from the GET arguments:
        redirect_url = urlparse(r1.url)
        logging.debug("Was redirected to SSO URL: %s" % str(redirect_url))

        # ...and inject them into the Kerberos authentication URL
        final_auth_url = "{auth_url}?{parameters}".format(
            auth_url=urljoin(r1.url, auth_url_fragment),
            parameters=redirect_url.query)

        return final_auth_url

    def _finalise_login(self, s, auth_results):
        """
        Perform the final POST authentication steps to fully authenticate
        the session, saving any cookies in s' cookie jar.
        """

        import xml.etree.ElementTree as ET

        r2 = auth_results

        # Did it work? Raise Exception otherwise.
        r2.raise_for_status()

        # Get the contents
        try:
            tree = ET.fromstring(r2.content)
        except ET.ParseError as e:
            logging.error("Could not parse response from server!")
            logging.error("The contents returned was:\n{}".format(r2.content))
            raise e

        action = tree.findall("body/form")[0].get('action')

        # Unpack the hidden form data fields
        form_data = dict((
            (elm.get('name'), elm.get('value'))
            for elm in tree.findall("body/form/input")))

        # ...and submit the form (WHY IS THIS STEP EVEN HERE!?)
        logging.debug("Performing final authentication POST to %s" % action)
        r3 = s.post(url = action, data = form_data, timeout = self.DEFAULT_TIMEOUT_SECONDS, allow_redirects=False)

        # Did _that_ work?
        r3.raise_for_status()

        # The session cookie jar should now contain the necessary cookies.
        logging.debug("Cookie jar now contains: %s" % str(s.cookies))

        return s.cookies

    def krb_sign_on(self, url, cookiejar={}, force_level = 0):
        """
        Perform Kerberos-backed single-sign on against a provided
        (protected) URL.

        It is assumed that the current session has a working Kerberos
        ticket.

        Returns a Requests `CookieJar`, which can be accessed as a
        dictionary, but most importantly passed directly into a request or
        session via the `cookies` keyword argument.

        If a cookiejar-like object (such as a dictionary) is passed as the
        cookiejar keword argument, this is passed on to the Session.
        """

        from requests_kerberos import HTTPKerberosAuth, OPTIONAL

        kerberos_auth = HTTPKerberosAuth(mutual_authentication=OPTIONAL)

        with requests.Session() as s:

            krb_auth_url = self._init_session(s=s, url=url, cookiejar=cookiejar,
                                        auth_url_fragment=u"auth/integrated/")

            # Perform actual Kerberos authentication
            logging.debug("Performing Kerberos authentication against %s"
                    % krb_auth_url)

            r2 = s.get(krb_auth_url, auth=kerberos_auth,
                      timeout=self.DEFAULT_TIMEOUT_SECONDS)

            return self._finalise_login(s, auth_results=r2)


    def cert_sign_on(self, url, cert_file, key_file, cookiejar={}):
        """
        Perform Single-Sign On with a robot/user certificate specified by
        cert_file and key_file agains the target url. Note that the key
        needs to be passwordless. cookiejar, if provided, will be used to
        store cookies, and can be a Requests CookieJar, or a
        MozillaCookieJar. Or even a dict.

        Cookies will be returned on completion, but cookiejar will also be
        modified in-place.

        If you have a PKCS12 (.p12) file, you need to convert it. These
        steps will not work for passwordless keys.

        `openssl pkcs12 -clcerts -nokeys -in myCert.p12 -out ~/private/myCert.pem`

        `openssl pkcs12 -nocerts -in myCert.p12 -out ~/private/myCert.tmp.key`

        `openssl rsa -in ~/private/myCert.tmp.key -out ~/private/myCert.key`

        Note that the resulting key file is *unencrypted*!

        """

        with requests.Session() as s:

            # Set up the certificates (this needs to be done _before_ any
            # connection is opened!)
            s.cert = (cert_file, key_file)

            cert_auth_url = self._init_session(s=s, url=url, cookiejar=cookiejar,
                                          auth_url_fragment=u"auth/sslclient/")

            logging.debug("Performing SSL Cert authentication against %s"
                    % cert_auth_url)

            r2 = s.get(cert_auth_url, cookies = cookiejar, verify = False, timeout = self.DEFAULT_TIMEOUT_SECONDS)

            return self._finalise_login(s, auth_results=r2)

class CernLoginSSO:

    def file_mtime(self, file_path):
        try:
            return os.path.getmtime(file_path)
        except OSError:
            return -1

    def login(self, url, cache_file = ".session.cache", force_level = 0):

        import json
        from getpass import getpass
        from os import remove, path
        import requests
        import warnings
        from base64 import b64encode, b64decode
        from ilock import ILock
        from selenium import webdriver
        from selenium.webdriver.firefox.options import Options
        from selenium.webdriver.support.ui import WebDriverWait
        from selenium.webdriver.support import expected_conditions as EC

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

                options = Options()
                options.headless = True
                driver = webdriver.Firefox(options = options)
                driver.get(url)

                in_login = driver.find_element_by_xpath("//td[@class='box_login']//input")
                in_login.clear()
                in_login.send_keys(username)
                in_passw = driver.find_element_by_xpath("//td[@class='box_password']//input")
                in_passw.clear()
                in_passw.send_keys(password)
                in_submit = driver.find_element_by_xpath("//td[@class='box_signinbutton']//input")
                in_submit.click()

                WebDriverWait(driver, 10).until(EC.url_to_be(url))

                cache = {
                    'secret': b64encode((username + '/' + b64encode(password.encode()).decode()).encode()).decode(),
                    'location': driver.current_url,
                    'cookies': { i['name']: i['value'] for i in driver.get_cookies() }
                }

                driver.close()

                with open(cache_file, 'w') as f:
                    f.write(json.dumps(cache))

        return cache['cookies']

class LoaderClient:

    USAGE = 'usage: %prog --url=URL FILE'
    ALLOWED_EXTENSIONS = ['xml','zip']
    
    def __init__(self):
        
        self.parser = OptionParser(self.USAGE)
        self.parser.add_option("-u", "--url",     dest = "url",     help = "service URL", metavar = "url")
        self.parser.add_option("-o", "--login",   dest = "login",   help = "use simple login provider cache (requires selenium, stores pwd in not secure way!)", metavar = "login", action = "store_true", default = False)
        self.parser.add_option("-k", "--krb",     dest = "krb",     help = "use kerberos login provider", metavar = "krb", action = "store_true", default = False)
        self.parser.add_option("-t", "--cert",    dest = "cert",    help = "pem certificate and key files in form cert_file:key_file", metavar = "cert")
        self.parser.add_option("-q", "--quiet",   dest = "quiet",   help = "Do not print error, just return its code. OK = 0", action = "store_true", default = False)
        self.parser.add_option("-v", "--verbose", dest = "verbose", help = "Print debug information (verbose output). Be carefull: this might expose password to terminal!", action = "store_true", default = False)

    def _allowed_file(self, filename):
        return '.' in filename and filename.rsplit('.', 1)[1] in self.ALLOWED_EXTENSIONS

    def run(self, iargs = []):

        SSO_LOGIN_URL = "https://login.cern.ch/"

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
                if sum((options.login, options.krb, options.cert is not None)) != 1:
                    self.parser.error('For secure access please provide one of --krb, --cert or --login')
                    return 1

            load_url = url + "/load/file"
            logging.debug("load_url = %s", load_url)

            if len(args) != 1:
                self.parser.error('Please provide one data file?')
                return 1

            f = args[0]
            if not (os.path.isfile(f) and os.path.exists(f)):
                self.parser.error('File [%s] not found or is not a file?' % f)
                return 2

            if not self._allowed_file(f):
                self.parser.error('File [%s] allowed? Allowed extensions are (%s)' % (f, ','.join(self.ALLOWED_EXTENSIONS)))
                return 2

            files = {'uploadFile': open(f, 'rb')}
            force_level = 0
            while True:

                cookies = None
                if re.search("^https", url):
                    if options.login:
                        cookies = CernLoginSSO().login(url, force_level = force_level)
                    if options.krb:
                        cookies = CernSSO().krb_sign_on(url)
                        force_level = 2
                    if options.cert is not None:
                        cert_file, key_file = options.cert.split(':')
                        cookies = CernSSO().cert_sign_on(url, cert_file, key_file)
            
                r = requests.post(url = load_url, files = files, cookies = cookies, verify = False)

                if r.status_code == 200 and r.url.startswith(SSO_LOGIN_URL):
                    if force_level < 2:
                        force_level = force_level + 1
                        continue
                    else:
                        if not options.login and not options.krb:
                            raise Exception('Resource is secured by SSO. Please try --login or --krb or --cert')
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

