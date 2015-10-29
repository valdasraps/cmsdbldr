#!/bin/bash

if [ -f target/cmsdbldr.jar ]; then
  
  version=`java -jar target/cmsdbldr.jar --version | sed 's/^.* //g'`
  zipname="cmsdbldr_${version}.zip"
  rm -f $zipname
  zip -q -9 -j -r $zipname src/release/resources/* target/cmsdbldr.jar
  echo $zipname

fi
