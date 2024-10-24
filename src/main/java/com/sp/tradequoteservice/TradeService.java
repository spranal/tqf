package com.sp.tradequoteservice;

import com.sp.tradequoteservice.model.Output;
import com.sp.tradequoteservice.model.Trade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TradeService {
    private static final Logger log = LogManager.getLogger(TradeService.class);
    private final long MAX_CACHE_SIZE = 1000000L;
    private final Object DUMMY = new Object();
    private final QuoteService quoteService;
    private final OutputService outputService;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private final Map<Trade, Object> unprocessedTrades =
            Collections.synchronizedMap(
                    new LinkedHashMap<>() {
                        @Override
                        protected boolean removeEldestEntry(Map.Entry<Trade, Object> entry) {
                            boolean thresholdExceeded = size() > MAX_CACHE_SIZE;
                            if (thresholdExceeded) {
                                log.error("Removing least recently inserted trade from cache to avoid memory failure.\n", entry.getKey());
                                //In addition to logging details, we should write this exception to persistent store to be analyzed and processed
                                //in an exception workflow.
                            }
                            return thresholdExceeded; //This will remove the least recently inserted trade if set size exceeds threshold of MAX_CACHE_SIZE.
                        }
                    });

    private final Runnable UNPROCESSED_TRADES_TASK = () -> {
        for(Iterator<Map.Entry<Trade, Object>> it = unprocessedTrades.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Trade, Object> entry = it.next();
            Trade unprocessedTrade = entry.getKey();
            log.info("Processing unprocessed trade {}", unprocessedTrade);
            if(processTradeOffline(unprocessedTrade)) {
                it.remove();
            }
        }
    };

    public TradeService(QuoteService quoteService, OutputService outputService) {
        this.quoteService = quoteService;
        this.outputService = outputService;
    }

    public void start() {
        executorService.scheduleWithFixedDelay(UNPROCESSED_TRADES_TASK, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void onMessage(Trade trade) {
        if(!enrichAndPublishTradeFromQuote(trade)) {
            //No mid-price avaiable. This could be due to delay in arriving quotes.
            //Write this into unprocessed set and process it offline via another thread
            //w/o impacting the core trade message processing flow
            unprocessedTrades.put(trade, DUMMY);
        } else {
            //In case that the trade feed comes from a single source implying
            //a possible 1:1 correlation between a trade and a quote, we can
            //remove the entries from the quote store as soon as the trade
            //is processed
            //quoteService.cleanupProcessedQuote();???
        }
    }

    private boolean enrichAndPublishTradeFromQuote(Trade trade) {
        BigDecimal midPx = quoteService.getMidPxForTimestampAndInstrumentId(trade.timeStamp(), trade.instrumentId());
        if(midPx != null) {
            log.info("Publishing trade quote in realtime");
            Output out = new Output(trade.timeStamp(), trade.instrumentId(), trade.customerId(), trade.sell(), trade.aggrInd(),
                    trade.tradePx(), trade.tradeVol(), midPx);
            outputService.publish(out);
            return true;
        } else {
            log.info("Deferring publishing trade quote in realtime as quote store is missing quote for timestamp {} and instrument {}",
                    trade.timeStamp(), trade.instrumentId());
        }
        return false;
    }

    public void stop() {
        for(Trade unprocessedTrade : unprocessedTrades.keySet()) {
            log.info("Processing trade offline {}", unprocessedTrade);
            processTradeOffline(unprocessedTrade);
        }
        executorService.shutdown();
    }

    private boolean processTradeOffline(Trade trade) {
        BigDecimal midPx = quoteService.getMidPxForTimestampAndInstrumentId(trade.timeStamp(), trade.instrumentId());
        if(midPx != null) {
            log.info("Publishing trade quote offline");
            Output out = new Output(trade.timeStamp(), trade.instrumentId(), trade.customerId(), trade.sell(), trade.aggrInd(),
                    trade.tradePx(), trade.tradeVol(), midPx);
            outputService.publish(out);
            return true;
        } else {
            log.info("Deferring publishing trade quote offline as quote store is still missing quote for timestamp {} and instrument {}",
                    trade.timeStamp(), trade.instrumentId());
        }
        return false;
    }
}
