package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsLoginCredentialHandler
{
    NcsPacket createLoginRequest();
    boolean isLoggedIn();
    boolean isLoginPacket(NcsPacket _pkt);
    boolean login(NcsPacket _pkt);
}
