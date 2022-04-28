package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsClientAuthorizationProvider
{
    boolean isAuthorizationRequest(NcsPacket _authorizationRequest);
    NcsPacket getClientAuthorization(NcsPacket _authorizationRequest);
}
