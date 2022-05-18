#!/usr/bin/env bash

XDIR=$(cd $(dirname $0) && pwd)

CKEY=$XDIR/test-client.key
CCRT=$XDIR/test-client.crt

IKEY=$XDIR/test-invalid.key
ICRT=$XDIR/test-invalid.crt

SKEY=$XDIR/test-server.key
SCRT=$XDIR/test-server.crt

certtool -p --bits 2048 > $SKEY
certtool -p --bits 2048 > $CKEY
certtool -p --bits 2048 > $IKEY

cat > $SCRT.tmpl <<_EOT_
#
# X.509 Certificate options
#
# DN options
organization = "org"
unit = "unit"
state = "city"
country = XX

cn = "server.example.com"

expiration_days = 7320
#activation_date = "2004-02-29 16:21:42"
#expiration_date = "2030-01-31 12:34:56"

# X.509 v3 extensions

# A dnsname in case of a WWW server.
dns_name = "www.server.example.com"
dns_name = "*.server.example.com"

tls_www_server
tls_www_client

signing_key
encryption_key
_EOT_

cat > $CCRT.tmpl <<_EOT_
cn = "client.example.com"
expiration_days = 90
tls_www_client
signing_key
encryption_key
_EOT_

cat > $ICRT.tmpl <<_EOT_
cn = "invalid.example.com"
expiration_days = 666
tls_www_client
signing_key
encryption_key
_EOT_

certtool -s --load-privkey $SKEY --template $SCRT.tmpl > $SCRT

certtool -c --load-privkey $CKEY --template $CCRT.tmpl --load-ca-privkey $SKEY --load-ca-certificate $SCRT > $CCRT

certtool -s --load-privkey $IKEY --template $ICRT.tmpl > $ICRT
