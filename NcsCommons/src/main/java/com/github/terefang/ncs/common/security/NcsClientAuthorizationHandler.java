package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsClientAuthorizationHandler
{
    NcsPacket createAuthorizationRequest();
    boolean isAuthorized();
    boolean isAuthorizationPacket(NcsPacket _pkt);
    boolean authorize(NcsPacket _pkt);
}
