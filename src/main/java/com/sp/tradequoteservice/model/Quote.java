package com.sp.tradequoteservice.model;

import java.math.BigDecimal;

public record Quote(long timeStamp, String instrumentId, int bidVol, BigDecimal bidPx, int askVol, BigDecimal askPx) { }
