package com.sp.tradequoteservice.service;

import com.sp.tradequoteservice.model.Quote;
import com.sp.tradequoteservice.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuoteServiceTest {
    private final String[] TEST_INSTRUMENTS = new String[] {"INST1", "INST2", "INST3", "INST4", "INST5"};
    private final int TEST_BID_VOL = 1000;
    private final int TEST_ASK_VOL = 1000;
    private final int TEST_TRADE_VOL = 100;
    private final String TEST_CUST_ID = "TEST_CUST";
    private QuoteService quoteService;
    private final Random random = new Random();
    private final ExceptionService exceptionService = new ExceptionService();

    @BeforeEach
    void setQuoteService() {
        quoteService = new QuoteService(exceptionService);
    }

    @Test
    void testMidPxForTimestampAndInstrumentIdForMatchingQuote() {
        Quote quote = new Quote(random.nextLong(), TEST_INSTRUMENTS[random.nextInt(5)], TEST_BID_VOL,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_ASK_VOL, BigDecimal.valueOf(random.nextDouble()*100.0));
        Trade trade = new Trade(quote.timeStamp(), quote.instrumentId(), TEST_CUST_ID,
                true, true, quote.bidPx(), TEST_TRADE_VOL);
        quoteService.onMessage(quote);
        BigDecimal actualMidPx = quoteService.getMidPxForTimestampAndInstrumentId(trade.timeStamp(), trade.instrumentId());
        BigDecimal expectedMidPx = quote.askPx().add(quote.bidPx()).divide(BigDecimal.valueOf(2.0));
        assertTrue(actualMidPx.equals(expectedMidPx));
    }

    @Test
    void testMidPxForTimestampAndInstrumentIdForNonMatchingQuoteOnTimestamp() {
        Quote quote = new Quote(random.nextLong(), TEST_INSTRUMENTS[random.nextInt(5)], TEST_BID_VOL,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_ASK_VOL, BigDecimal.valueOf(random.nextDouble()*100.0));
        Trade trade = new Trade(quote.timeStamp() - 1, quote.instrumentId(), TEST_CUST_ID,
                true, true, quote.bidPx(), TEST_TRADE_VOL);
        quoteService.onMessage(quote);
        BigDecimal actualMidPx = quoteService.getMidPxForTimestampAndInstrumentId(trade.timeStamp(), trade.instrumentId());
        assertNull(actualMidPx);
    }

    @Test
    void testMidPxForTimestampAndInstrumentIdForNonMatchingQuoteOnInstId() {
        Quote quote = new Quote(random.nextLong(), TEST_INSTRUMENTS[random.nextInt(5)], TEST_BID_VOL,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_ASK_VOL, BigDecimal.valueOf(random.nextDouble()*100.0));
        Trade trade = new Trade(quote.timeStamp(), quote.instrumentId() + "X", TEST_CUST_ID,
                true, true, quote.bidPx(), TEST_TRADE_VOL);
        quoteService.onMessage(quote);
        BigDecimal actualMidPx = quoteService.getMidPxForTimestampAndInstrumentId(trade.timeStamp(), trade.instrumentId());
        assertNull(actualMidPx);
    }

}