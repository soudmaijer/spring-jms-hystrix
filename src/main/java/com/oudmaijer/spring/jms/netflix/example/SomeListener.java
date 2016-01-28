package com.oudmaijer.spring.jms.netflix.example;


import com.oudmaijer.spring.jms.netflix.CircuitBreakerAwareJmsListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SomeListener {

    private static final Logger LOG = LogManager.getLogger();
    private final ExternalService externalService;

    @Autowired
    public SomeListener(ExternalService externalService) {
        this.externalService = externalService;
    }

    @JmsListener(destination = "myQueue", containerFactory = "jmsListenerContainerFactory", concurrency = "2-2")
    @CircuitBreakerAwareJmsListener(hystrixCommandKeys = {"someCommandKey1", "someCommandKey2"}, permitsPerSecond = 10)
    public void handleMessage(Message<String> message) {
        try {
            // do stuff that is depending on the someCommandKey to work
            externalService.doStuff1();
            externalService.doStuff2();

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}