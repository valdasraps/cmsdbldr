#!/bin/bash

export JAVA_HOME=/home/dbguru/app/jdk1.8.0_66
export PATH=$PATH:$JAVA_HOME/bin
export TNS_ADMIN=/etc

FNAME=`basename "$1"`
BASEF="`dirname "$1"`"
DATAB="`basename "$BASEF"`"
SUBDET="`basename \`dirname "$BASEF"\``"
FPATH="$SUBDET/$DATAB/$FNAME"
PREFIX=`date +%H%M%S.`

echo "FNAME   = $FNAME" 
echo "DATAB   = $DATAB"
echo "SUBDET  = $SUBDET"
echo "FPATH   = $FPATH"
echo "PREFIX  = $PREFIX"

SPOOL_D=/home/dbguru/dbspool/spool
STATE_D=/home/dbguru/dbspool/state
LOGS_D=/home/dbguru/dbspool/logs
WORK_D=/home/dbguru/work
TEMP_D="`mktemp -p \"${WORK_D}\" -d`"
LOG_D=/home/dbguru/logs/${SUBDET}/${DATAB}/`date +%Y%m%d`

echo "SPOOL_D = $SPOOL_D"
echo "STATE_D = $STATE_D"
echo "LOGS_D  = $LOGS_D"
echo "WORK_D  = $WORK_D"
echo "TEMP_D  = $TEMP_D"
echo "LOG_D   = $LOG_D"

SPOOL_F="${SPOOL_D}/${FPATH}"
STATE_F="${STATE_D}/${FPATH}"
LOGS_F="${LOGS_D}/${FPATH}"
WORK_F="${TEMP_D}/${FNAME}"
SAVE_F="${LOG_D}/${PREFIX}${FNAME}"
LOG_F="${SAVE_F}.log"
PROP_F="${SUBDET}_${DATAB}.properties"

echo "SPOOL_F = $SPOOL_F"
echo "STATE_F = $STATE_F"
echo "LOGS_F  = $LOGS_F"
echo "WORK_F  = $WORK_F"
echo "SAVE_F  = $SAVE_F"
echo "LOG_F   = $LOG_F"
echo "PROP_F  = $PROP_F"

shopt -s nocasematch
if [[ "$FNAME" =~ \.xml$ || "$FNAME" =~ \.zip$ ]]; then

  # Move cursor to application dir
  cd "$(dirname "$0")"

  # Check if properties file exits
  if test -r "$PROP_F" -a -f "$PROP_F"; then

    # Get file user
    $USER_F=`stat -c '%U' "$SPOOL_F"`

    # Move file to work area
    mv "$SPOOL_F" "$WORK_F"

    # Execute DB loader application
    mkdir -p "$LOG_D"
    ./cmsdbldr.sh --properties="$PROP_F" --file-user="$USER_F" "$WORK_F" >"$LOG_F" 2>&1

    echo $? >"$STATE_F"
    cp "$LOG_F" "$LOGS_F"

    # Move file to save
    mv "$WORK_F" "$SAVE_F"

  else

    echo 2 >"$STATE_F"
    echo "Properties file not found" >"$LOGS_F"

  fi

else

  echo 1 >"$STATE_F"
  echo "Unrecognized file" >"$LOGS_F"

fi

rm -fR "${TEMP_D}"

