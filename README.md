# Netty Client Server

a demo library that allows sending/receiving encoded packets on client and server side.

See the following classes for example:

* SimpleTestServer
* SimpleTestClient

## Libraries

* NcsCommons -- shared code between client and server implementations
* NcsClient -- client implementation based on netty nio (portable)
* NcsServer -- server implementation based on netty nio (portable) can be switched to epoll (linux x86_64)
* NcsLib -- additional classes developers might find useful
