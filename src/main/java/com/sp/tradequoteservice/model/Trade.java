package com.sp.tradequoteservice.model;

import java.math.BigDecimal;

public record Trade(long timeStamp, String instrumentId, String customerId, boolean sell, boolean aggrInd,
                    BigDecimal tradePx, int tradeVol) {
}
