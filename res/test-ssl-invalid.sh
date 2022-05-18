#!/usr/bin/env bash

XDIR=$(cd $(dirname $0) && pwd)

openssl s_client -connect 127.0.0.1:56789 -debug -showcerts -cert $XDIR/test-invalid.crt -key $XDIR/test-invalid.key -CAfile $XDIR/test-server.crt