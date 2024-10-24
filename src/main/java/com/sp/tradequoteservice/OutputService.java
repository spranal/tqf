package com.sp.tradequoteservice;

import com.sp.tradequoteservice.model.Message;
import com.sp.tradequoteservice.model.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class OutputService {
    private static final Logger log = LogManager.getLogger(OutputService.class);

    public void publish(Message out) {
        try {
            //Implement serializing and publishing the message out as per transport
            if(out instanceof Output o) {
                log.info("Publishing Trade Quote Output with timestamp {}: {}",o.timeStamp(), o);
            } else {
                log.error("Received message that is not of type Output");
            }
        } catch (Exception e) {
        }
    }
}
