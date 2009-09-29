#! /bin/bash

sh pax-runner-1.2.0/bin/pax-run.sh --vmOptions="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000" file:ogee-dirinstaller-0.0.2.jar@update
