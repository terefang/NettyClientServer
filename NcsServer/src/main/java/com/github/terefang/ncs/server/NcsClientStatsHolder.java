package com.github.terefang.ncs.server;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class NcsClientStatsHolder {
    AtomicInteger sentKeepAlives = new AtomicInteger(0);

    long currentRTT = 0L;
    long historicRTT = 0L;

    long lastTimestamp = -1L;


    public void incrSent()
    {
        this.sentKeepAlives.incrementAndGet();
    }

    public void resetAlives()
    {
        this.sentKeepAlives.set(0);
    }


    public void recalcRTT(long _t2, long _t1)
    {
        long _newRTT = _t2 - _t2;
        this.historicRTT = (this.currentRTT + this.historicRTT) >> 1;
        this.currentRTT = _newRTT;
        this.lastTimestamp = _t2;
    }
}
