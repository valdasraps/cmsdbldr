#!/bin/bash

cd "$(dirname "$0")"

sem --will-cite --id cmsdbldr -j 3 python runner.py $@
