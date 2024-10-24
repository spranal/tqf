package com.sp.tradequoteservice.service;

import com.sp.tradequoteservice.model.Message;
import com.sp.tradequoteservice.model.Publisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ExceptionService implements Publisher {
    private static final Logger log = LogManager.getLogger(ExceptionService.class);

    @Override
    public void publish(Message out) {
        log.info("Publishing to exception management system");
    }
}
