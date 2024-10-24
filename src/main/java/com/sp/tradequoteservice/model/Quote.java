package com.sp.tradequoteservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public final class Quote implements Message, Serializable {
    @Serial
    private static final long serialVersionUID = 20241024L;
    private final long timeStamp;
    private final String instrumentId;
    private final int bidVol;
    private final BigDecimal bidPx;
    private final int askVol;
    private final BigDecimal askPx;

    public Quote(long timeStamp, String instrumentId, int bidVol, BigDecimal bidPx, int askVol, BigDecimal askPx) {
        this.timeStamp = timeStamp;
        this.instrumentId = instrumentId;
        this.bidVol = bidVol;
        this.bidPx = bidPx;
        this.askVol = askVol;
        this.askPx = askPx;
    }

    public long timeStamp() {
        return timeStamp;
    }

    public String instrumentId() {
        return instrumentId;
    }

    public int bidVol() {
        return bidVol;
    }

    public BigDecimal bidPx() {
        return bidPx;
    }

    public int askVol() {
        return askVol;
    }

    public BigDecimal askPx() {
        return askPx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return timeStamp == quote.timeStamp && bidVol == quote.bidVol && askVol == quote.askVol
                && Objects.equals(instrumentId, quote.instrumentId) && Objects.equals(bidPx, quote.bidPx)
                && Objects.equals(askPx, quote.askPx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, instrumentId, bidVol, bidPx, askVol, askPx);
    }

    @Override
    public String toString() {
        return "Quote{" +
                "timeStamp=" + timeStamp +
                ", instrumentId='" + instrumentId + '\'' +
                ", bidVol=" + bidVol +
                ", bidPx=" + bidPx +
                ", askVol=" + askVol +
                ", askPx=" + askPx +
                '}';
    }
}
