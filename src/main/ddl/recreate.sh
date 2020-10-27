#!/bin/sh

sqlplus64 system/rootas@xemif @drop
sqlplus64 system/rootas@xemif @create xemif
sqlplus64 system/rootas@xemif @create_tracking xemif
sqlplus64 system/rootas@xemif @create_assembly xemif
cd ../../test/ddl
sqlplus64 system/rootas@xemif @create xemif
cd ../../main/ddl
