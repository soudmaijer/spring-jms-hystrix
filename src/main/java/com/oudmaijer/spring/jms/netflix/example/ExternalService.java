package com.oudmaijer.spring.jms.netflix.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ExternalService {

    private static final Logger LOG = LogManager.getLogger();
    private static AtomicInteger atomicInteger = new AtomicInteger();

    @HystrixCommand(groupKey = "ExternalService", commandKey = "someCommandKey1")
    public void doStuff1() {
        int i = atomicInteger.incrementAndGet();
        LOG.info("Processing message {}", i);
        if( i>100 && i<350 ) {
            LOG.info("Exception in message {}", i);
            throw new RuntimeException("BOEM");
        }
    }

    @HystrixCommand(groupKey = "ExternalService", commandKey = "someCommandKey2")
    public void doStuff2() {
        //System.err.println("doStuff2");
    }
}
