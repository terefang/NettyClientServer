package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.channel.Channel;
import lombok.SneakyThrows;

/**
 * represents a connection to a peer
 */
public interface NcsConnection
{
    /**
     * allows setting a custom context on this connection
     * @param _context the context
     */
    void setContext(Object _context);

    /**
     * allow retrieving a custon context from this connection
     * @param _clazz the context-class
     * @return the context
     */
    <T> T getContext(Class<T> _clazz);

    /**
     * retrieves the associated peer of the connection
     * @return the peers endpoint
     */
    NcsEndpoint getPeer();

    /**
     * sends a packet to peer on the connection
     * @param _pkt the packet
     */
    void send(NcsPacket _pkt);

    /**
     * sends a packet to peer on the connection
     * flushes the queue
     * @param _pkt the packet
     */
    void sendAndFlush(NcsPacket _pkt);

    void flush();

    boolean isUdp();

    void setChannel(Channel _channel);

    Channel getChannel();

    @SneakyThrows
    default void close()
    {
        if(!isUdp())
        {
            this.getChannel().close().sync();
        }
        else
        {
            throw new UnsupportedOperationException("unimplemented from here");
        }
    }

    boolean isClientMode();
}
