#!/bin/sh -e

cd src/main/ddl
"$ORACLE_HOME/bin/sqlplus" -L / AS SYSDBA @create.sql

cd ../../test/ddl
"$ORACLE_HOME/bin/sqlplus" -L / AS SYSDBA @create.sql
