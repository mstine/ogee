#! /bin/bash

mvn -o package -Ddev && cd distribution/target/ogee-distribution-0.0.2-runtime.dir/;rlwrap -c pax-run.sh scan-dir:.@update;cd ../../../

