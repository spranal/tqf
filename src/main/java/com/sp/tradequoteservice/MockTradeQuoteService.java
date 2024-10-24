package com.sp.tradequoteservice;

import com.sp.tradequoteservice.model.Quote;
import com.sp.tradequoteservice.model.Trade;
import com.sp.tradequoteservice.service.QuoteService;
import com.sp.tradequoteservice.service.TradeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MockTradeQuoteService {
    private static final Logger log = LogManager.getLogger(MockTradeQuoteService.class);
    private QuoteService quoteService;
    private TradeService tradeService;
    private final int MOCK_DATA_SIZE = 100;
    private final int MOCK_BID_VOL = 100000;
    private final int MOCK_ASK_VOL = 150000;
    private final int MOCK_TRADE_VOL = 100;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Quote[] MOCK_QUOTES = new Quote[MOCK_DATA_SIZE];
    private final Trade[] MOCK_TRADES = new Trade[MOCK_DATA_SIZE];
    private final String MOCK_CUST_ID = "MOCK_CUST";
    private final boolean MOCK_AGG_IND = false;
    private final String[] MOCK_INSTRUMENTS = new String[] {"INST1", "INST2", "INST3", "INST4", "INST5"};
    private final Random random = new Random();
    private final AtomicInteger QUOTE_FEED_COUNTER = new AtomicInteger(1);
    private final AtomicInteger TRADE_FEED_COUNTER = new AtomicInteger(1);
    private final Runnable QUOTE_FEED_TASK = () -> {
        //This can be made more generic by pumping data in an infinite loop.
        //but to make things simpler, having a bounded data pump
        for(Quote quote : MOCK_QUOTES) {
            try {
                log.info("Pumping quote no {}", QUOTE_FEED_COUNTER.getAndIncrement());
                Thread.sleep(random.nextInt(250));
                quoteService.onMessage(quote);
            } catch (InterruptedException e) {
                log.error("Quote feed task threw exception", e);
                throw new RuntimeException(e);
            }
        }
    };

    private final Runnable TRADE_FEED_TASK = () -> {
        //This can be made more generic by pumping data in an infinite loop.
        //but to make things simpler, having a bounded data pump
        for(Trade trade : MOCK_TRADES) {
            try {
                log.info("Pumping trade no {}", TRADE_FEED_COUNTER.getAndIncrement());
                tradeService.onMessage(trade);
                Thread.sleep(random.nextInt(250));
            } catch (Exception e) {
                log.error("Trade feed task threw exception", e);
                throw new RuntimeException(e);
            }
        }
    };

    public MockTradeQuoteService(QuoteService quoteService, TradeService tradeService) {
        this.quoteService = quoteService;
        this.tradeService = tradeService;
        //Populate mock data to be able to pump into quote service and trade service
        //This can be made more generic by pumping data in an infinite loop.
        //but to make things simpler, having a bounded data pump
        for(int i=0; i<MOCK_DATA_SIZE; i++) {
            MOCK_QUOTES[i] = new Quote(random.nextLong(Long.MAX_VALUE), MOCK_INSTRUMENTS[random.nextInt(5)], MOCK_BID_VOL,
                    BigDecimal.valueOf(random.nextDouble()*100.0), MOCK_ASK_VOL, BigDecimal.valueOf(random.nextDouble()*100.0));
            boolean sell = random.nextBoolean();
            BigDecimal tradePx;
            if(sell) {
                tradePx = MOCK_QUOTES[i].bidPx();
            } else {
                tradePx = MOCK_QUOTES[i].askPx();
            }
            MOCK_TRADES[i] = new Trade(MOCK_QUOTES[i].timeStamp(), MOCK_QUOTES[i].instrumentId(), MOCK_CUST_ID,
                    sell, MOCK_AGG_IND, tradePx, MOCK_TRADE_VOL);
        }
    }

    void start() throws InterruptedException {
        log.info("Trades to be published: ");
        for(Trade trade : MOCK_TRADES) {
            log.info("Timestamp {}, Trade {}", trade.timeStamp(), trade);
        }
        log.info("\n");
        log.info("Available quotes: ");
        for(Quote quote :  MOCK_QUOTES) {
            log.info("Timestamp {}, Quote {}", quote.timeStamp(), quote);
        }
        log.info("\n");
        tradeService.start();
        Thread tradeFeedThread = new Thread(TRADE_FEED_TASK);
        Thread quoteFeedThread = new Thread(QUOTE_FEED_TASK);
        tradeFeedThread.start();
        quoteFeedThread.start();
        tradeFeedThread.join();
        quoteFeedThread.join();
        stop();
    }

    void stop() {
        tradeService.stop();
        executorService.shutdown();
    }
}
