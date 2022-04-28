package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsLoginCredentialProvider
{
    boolean isLoginRequest(NcsPacket _loginRequest);
    NcsPacket getLoginCredential(NcsPacket _loginRequest);
}
