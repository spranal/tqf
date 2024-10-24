package com.sp.tradequoteservice.service;

import com.sp.tradequoteservice.model.Output;
import com.sp.tradequoteservice.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {
    private TradeService tradeService;
    @Mock
    private QuoteService quoteService;
    @Mock
    private OutputService outputService;
    @Mock
    private ExceptionService exceptionService;
    private final String TEST_INSTR_ID = "INST1";
    private final String TEST_CUST_ID = "CUST1";
    private final int TEST_TRADE_VOL = 100;
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        tradeService = new TradeService(quoteService, outputService, exceptionService);
    }

    @Test
    void testOnMessageWhenTradeHasMatchingQuote() {
        long timestamp = random.nextLong();
        when(quoteService.getMidPxForTimestampAndInstrumentId(timestamp, TEST_INSTR_ID))
                .thenReturn(BigDecimal.valueOf(random.nextDouble()*100.0));
        tradeService.onMessage(new Trade(timestamp, TEST_INSTR_ID, TEST_CUST_ID, false, true,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_TRADE_VOL));
        verify(outputService, times(1)).publish(any(Output.class));
        assertTrue(tradeService.getUnprocessedTradeCount() == 0);
    }

    @Test
    void testOnMessageWhenTradeHasNoMatchingQuote() {
        tradeService.onMessage(new Trade(random.nextLong(), TEST_INSTR_ID, TEST_CUST_ID, false, true,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_TRADE_VOL));
        verifyNoInteractions(outputService);
        assertTrue(tradeService.getUnprocessedTradeCount() == 1);
    }

    @Test
    void testOnMessageExceedingCacheSize() {
        tradeService.setMaxCacheSize(1);
        tradeService.onMessage(new Trade(random.nextLong(), TEST_INSTR_ID, TEST_CUST_ID, false, true,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_TRADE_VOL));
        tradeService.onMessage(new Trade(random.nextLong(), TEST_INSTR_ID, TEST_CUST_ID, false, true,
                BigDecimal.valueOf(random.nextDouble()*100.0), TEST_TRADE_VOL));
        assertTrue(tradeService.getExceptionTradeCount() == 1);
    }

}