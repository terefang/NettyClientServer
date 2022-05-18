#!/usr/bin/env bash

XDIR=$(cd $(dirname $0) && pwd)

openssl s_client -connect 127.0.0.1:56789 -debug -showcerts -cert $XDIR/test-client.crt -key $XDIR/test-client.key -CAfile $XDIR/test-server.crt