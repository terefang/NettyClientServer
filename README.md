# Netty Client Server

a demo library that allows sending/receiving encoded packets on client and server side.

See the following classes for example:

* SimpleTestServer
* local.ncs.tcp.SimpleTestClient

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
