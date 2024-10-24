package com.sp.tradequoteservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public final class Trade implements Message, Serializable {
    @Serial
    private static final long serialVersionUID = 20241024L;
    private final long timeStamp;
    private final String instrumentId;
    private final String customerId;
    private final boolean sell;
    private final boolean aggrInd;
    private final BigDecimal tradePx;
    private final int tradeVol;

    public Trade(long timeStamp, String instrumentId, String customerId, boolean sell, boolean aggrInd,
                 BigDecimal tradePx, int tradeVol) {
        this.timeStamp = timeStamp;
        this.instrumentId = instrumentId;
        this.customerId = customerId;
        this.sell = sell;
        this.aggrInd = aggrInd;
        this.tradePx = tradePx;
        this.tradeVol = tradeVol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return timeStamp == trade.timeStamp && sell == trade.sell && aggrInd == trade.aggrInd && tradeVol == trade.tradeVol && Objects.equals(instrumentId, trade.instrumentId) && Objects.equals(customerId, trade.customerId) && Objects.equals(tradePx, trade.tradePx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeStamp, instrumentId, customerId, sell, aggrInd, tradePx, tradeVol);
    }

    public long timeStamp() {
        return timeStamp;
    }

    public String instrumentId() {
        return instrumentId;
    }

    public String customerId() {
        return customerId;
    }

    public boolean sell() {
        return sell;
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

    @Override
    public String toString() {
        return "Trade{" +
                "timeStamp=" + timeStamp +
                ", instrumentId='" + instrumentId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", sell=" + sell +
                ", aggrInd=" + aggrInd +
                ", tradePx=" + tradePx +
                ", tradeVol=" + tradeVol +
                '}';
    }
}
