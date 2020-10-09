CMS DB Loader application V2
=======

[![Coverage Status](https://coveralls.io/repos/valdasraps/cmsdbldr/badge.svg?branch=master&service=github)](https://coveralls.io/github/valdasraps/cmsdbldr?branch=master)
[![Build Status](https://travis-ci.org/valdasraps/cmsdbldr.svg?branch=master)](https://travis-ci.org/valdasraps/cmsdbldr)

#### Overview

Uploads XML files to Oracle DB. XML files can be found in exmaple folder.

#### Help
```
usage: org.cern.cms.dbloader.DbLoader
    --attribute-core-schema <arg>     MANAGEMNT: attribute schema name
    --channel-class <arg>             print generated channel class
    --channel-desc <arg>              describe single channel
    --channel-list                    list available channels
    --cond-class <arg>                print generated condition class
    --cond-dataset <arg>              get XML stream data
    --cond-datasets <arg>             list condition dataset
    --cond-desc <arg>                 describe single condition
    --cond-list                       list available conditions
    --cond-xml <arg>                  print condition XML example
    --condition-core-schema <arg>     CONDITION: core schema name
    --condition-ext-schema <arg>      CONDITION: extension schema name
    --construct-core-schema <arg>     CONSTRUCT: core schema name
    --construct-ext-schema <arg>      CONSTRUCT: extension schema name
    --help                            print usage
    --iov-core-schema <arg>           MANAGEMNT: iov schema name
    --log-level <arg>                 log level, possible values
                                      OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRAC
                                      E. Default is ERROR
    --managemnt-core-schema <arg>     MANAGEMNT: core schema name
    --operator-condition-permission   operator condition permission
    --operator-construct-permission   operator construct permission
    --operator-fullname <arg>         operator fullname, DEFAULT: operator
                                      user
    --operator-tracking-permission    operator tracking permission
    --operator-username <arg>         operator username, DEFAULT: operator
                                      user
    --password <arg>                  database password
    --print-sql                       print SQL queries to stdout
    --properties <arg>                path to connection properties file
    --root-part-id <arg>              ROOT Part ID
    --schema <arg>                    save XML schema files to path
    --test                            upload test - proceed with the full
                                      upload process but rollback the
                                      transaction
    --url <arg>                       database connection URL
    --username <arg>                  database username
    --version                         print version
    --view-ext-schema <arg>           VIEW: extension schema name
```
