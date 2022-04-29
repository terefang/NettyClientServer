#!/usr/bin/env bash

XDIR=$(cd $(dirname $0) && pwd)

openssl s_server -port 56789 -debug -cert $XDIR/test-server.crt -key $XDIR/test-server.key -CAfile /etc/ssl/certs/ca-certificates.crt