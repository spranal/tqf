package com.sp.tradequoteservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public final class Output implements Message, Serializable {
    @Serial
    private static final long serialVersionUID = 20241024L;
    final long timeStamp;
    final String instrumentId;
    final String customerId;
    final boolean side;
    final boolean aggrInd;
    final BigDecimal tradePx;
    final int tradeVol;
    final BigDecimal midPx;

    public long timeStamp() {
        return timeStamp;
    }

    public String instrumentId() {
        return instrumentId;
    }

    public String customerId() {
        return customerId;
    }

    public boolean side() {
        return side;
    }

    public boolean aggrInd() {
        return aggrInd;
    }

    public BigDecimal tradePx() {
        return tradePx;
    }

    public int tradeVol() {
        return tradeVol;
    }

    public BigDecimal midPx() {
        return midPx;
    }

    public Output(long timeStamp, String instrumentId, String customerId, boolean side, boolean aggrInd,
                  BigDecimal tradePx, int tradeVol, BigDecimal midPx) {
        this.timeStamp = timeStamp;
        this.instrumentId = instrumentId;
        this.customerId = customerId;
        this.side = side;
        this.aggrInd = aggrInd;
        this.tradePx = tradePx;
        this.tradeVol = tradeVol;
        this.midPx = midPx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Output output = (Output) o;
        return timeStamp == output.timeStamp && side == output.side && aggrInd == output.aggrInd
                && tradeVol == output.tradeVol && Objects.equals(instrumentId, output.instrumentId)
                && Objects.equals(customerId, output.customerId) && Objects.equals(tradePx, output.tradePx)
                && Objects.equals(midPx, output.midPx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, instrumentId, customerId, side, aggrInd, tradePx, tradeVol, midPx);
    }

    @Override
    public String toString() {
        return "Output{" +
                "timeStamp=" + timeStamp +
                ", instrumentId='" + instrumentId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", side=" + side +
                ", aggrInd=" + aggrInd +
                ", tradePx=" + tradePx +
                ", tradeVol=" + tradeVol +
                ", midPx=" + midPx +
                '}';
    }
}

