package com.oudmaijer.spring.jms.netflix.example;


import com.oudmaijer.spring.jms.netflix.CircuitBreakerAwareJmsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SomeListener {

    private final ExternalService externalService;

    @Autowired
    public SomeListener(ExternalService externalService) {
        this.externalService = externalService;
    }

    @JmsListener(destination = "myQueue", containerFactory = "jmsListenerContainerFactory")
    @CircuitBreakerAwareJmsListener(hystrixCommandKeys = {"someCommandKey1", "someCommandKey2"}, permitsPerSecond = 1)
    public void handleMessage(Message<String> message) {
        // do stuff that is depending on the someCommandKey to work
        externalService.doStuff1();
        externalService.doStuff2();
        System.err.println("BAM!");
    }
}