#!/bin/bash

if type -p java; then
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    _java="$JAVA_HOME/bin/java"
else
    echo "Java not found. Please install Java 1.7+ and try again"
    exit 1
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" < "1.7" ]]; then
        echo "Java version not sufficient. Please install Java 1.7+ and try again"
        exit 1
    fi
fi

_args=-jar
_exec=cmsdbldr.jar

if [[ -n "$TNS_ADMIN" ]] && [[ -r "$TNS_ADMIN/tnsnames.ora" ]];  then
    _args="$_args -Doracle.net.tns_admin=$TNS_ADMIN"
elif [[ -r "/etc/tnsnames.ora" ]];  then
    _args="$_args -Doracle.net.tns_admin=/etc"
fi

$_java $_args $_exec $@
