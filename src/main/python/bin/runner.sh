#!/bin/bash

cd "$(dirname "$0")"

python runner.py $@ >>/var/cmsdbldr/runner.log 2>>/var/cmsdbldr/runner.err
