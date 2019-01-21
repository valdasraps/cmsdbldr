Creates all database objects.

Steps:

1. edit params.sql

2. execute: sqlplus / as sysdba @create <sid_url>

To remove everything execute: sqlplus / as sysdba @drop

i.e. 

sqlplus64 sys/password@localhost:1521/xe as sysdba @create localhost:1521/xe
