#!/bin/sh -e

cd src/main/ddl
"$ORACLE_HOME/bin/sqlplus" -L / AS SYSDBA @create.sql xe
"$ORACLE_HOME/bin/sqlplus" -L / AS SYSDBA @create_tracking.sql xe

cd ../../test/ddl
"$ORACLE_HOME/bin/sqlplus" -L / AS SYSDBA @create.sql xe
