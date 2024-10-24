package com.sp.tradequoteservice;

import com.sp.tradequoteservice.model.Quote;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class QuoteService {
    private static final Logger log = LogManager.getLogger(QuoteService.class);
    private final long MAX_CACHE_SIZE = 1000000L; //this should be set according to the throughput of the quote feed
                                                  //so that the candidate entries are stale enough that its unlikely that a
                                                  //trade would arrive on this quote anymore
    private final Map<Long, Map<String, Quote>> quoteStore =
            Collections.synchronizedMap(
                    new LinkedHashMap<>() {
                        @Override
                        protected boolean removeEldestEntry(Map.Entry<Long, Map<String, Quote>> entry) {
                            boolean thresholdExceeded = size() > MAX_CACHE_SIZE;
                            if (thresholdExceeded) {
                                log.info("Removing stale quote with timestamp {} from cache to remain bounded.\n", entry.getKey());
                                log.info("Quote Details: ");
                                for (Quote quote : entry.getValue().values()) {
                                    log.info(quote);
                                }
                                //In addition to logging details, we should write this to persistent/archive store to be used in
                                //the analysis and processing of trade feed exception workflow where we look at trades that were
                                //not sent out due to unavailability of corresponding quote
                            }
                            return thresholdExceeded; //This will remove the least recently inserted key
                            //if map size exceeds threshold of 1million.
                        }
                    });

    public void onMessage(Quote quote) {
        if(quote != null) {
            quoteStore.computeIfAbsent(quote.timeStamp(), k -> new HashMap<>()).put(quote.instrumentId(), quote);
        } else {
            log.error("Null Quote received from quote feed");
        }
    }

    //This method gets the quote for a given timestamp if its
    //present in the quote store to cater to the following requirement
    //****The mid_px should be taken from the prevailing record of the QUOTE table****/
    //****at the time given in the TRADE record, for the same instrument**************/
    //Making an assumption here that "at the time" implies the trade timestamp being
    //captured in the trade corresponds to the quote timestamp and not the input time of the trade
    //******************************************************************************************/
    //Removing the above assumption would mean we store the incoming quote data in a sorted data structure
    //like TreeMap where it is efficient to search for top quote that is closest to the timestamp on the trade
    //The quote closest imply the one that matches the trade price and has a timestamp nearest to the trade
    //Again this presents more questions as to what if two quotes within a time window has the same price
    //as trade price but having a different counterpart price (bid vs ask). So in this case, what if the
    //actual quote that was used actually hasn't arrived yet, and we landed up using an old one that has
    //matching trade price but different counterpart price. Hence to make things simpler, the assumption
    //that the trade timestamp has 1:1 mapping with the quote timestamp was made here.
    public BigDecimal getMidPxForTimestampAndInstrumentId(long timeStamp, String instrumentId) {
        if(instrumentId != null) {
            Map<String, Quote> quoteMap = quoteStore.get(timeStamp);
            if(quoteMap != null) {
                Quote quote = quoteMap.get(instrumentId);
                if(quote != null) {
                    //Mid-price is calculated as mean of the best bid and ask in the quote
                    return quote.askPx().add(quote.bidPx()).divide(BigDecimal.valueOf(2.0));
                }
            }
        } else {
            log.error("null instrumentId passed in for getQuote");
        }
        return null;
    }
}
