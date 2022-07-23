package com.github.terefang.ncs.common.security.obf;

public interface NcsPskObfCodec {

    NcsPskObfCodecState getState();

    void setState(NcsPskObfCodecState _state);

    default boolean isTolerant() {
        return getState().isTolerant();
    }

    default  void setTolerant(boolean tolerant)
    {
        getState().setTolerant(tolerant);
    }

    default  void newState(String _sharedSecret, int _max, boolean _useObf, boolean _useCRC) {
        setState(NcsPskObfCodecState.from(_sharedSecret, _max, _useObf, _useCRC));
    }
}
