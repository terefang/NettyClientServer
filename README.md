# Netty Client Server

a demo library that allows sending/receiving encoded packets on client and server side.

See the following classes for example:

* local.ncs.tcp.SimpleTestServer
* local.ncs.tcp.SimpleTestClient

## JAVA Version

The library is currently developed and tested with JDK/JRE 8 only, 
but should run on JDK 9+ if appropriate actions are taken.

## Compatiblity

The library is not intended for use on GWT, and never will be.

## Libraries

* NcsCommons -- shared code between client and server implementations
* NcsClient -- client implementation based on netty nio (portable)
* NcsServer -- server implementation based on netty nio (portable) can be switched to epoll (linux x86_64)
* NcsLib -- additional classes developers might find useful

## Usage

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.terefang.NettyClientServer</groupId>
        <artifactId>NcsServer</artifactId>
        <version>${ncs.version}</version>
    </dependency>
</dependencies>
```

```xml
<dependencies>
    <dependency>
        <groupId>com.github.terefang.NettyClientServer</groupId>
        <artifactId>NcsClient</artifactId>
        <version>${ncs.version}</version>
    </dependency>
</dependencies>
```

## Bugs and Issues

If you find a bug, please fix it and send patch to https://github.com/terefang/NettyClientServer/issues

