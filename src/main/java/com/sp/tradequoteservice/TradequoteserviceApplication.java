package com.sp.tradequoteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TradequoteserviceApplication {

	public static void main(String[] args) {
		ApplicationContext appCtx = SpringApplication.run(TradequoteserviceApplication.class, args);
		MockTradeQuoteService mockTradeQuoteService = appCtx.getBean(MockTradeQuoteService.class);
        try {
            mockTradeQuoteService.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
