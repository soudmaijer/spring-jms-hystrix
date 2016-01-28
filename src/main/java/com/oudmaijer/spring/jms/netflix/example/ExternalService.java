package com.oudmaijer.spring.jms.netflix.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

@Component
public class ExternalService {

    @HystrixCommand(groupKey = "ExternalService", commandKey = "someCommandKey1")
    public void doStuff1() {
        System.err.println("doStuff1");
    }

    @HystrixCommand(groupKey = "ExternalService", commandKey = "someCommandKey2")
    public void doStuff2() {
        System.err.println("doStuff2");
    }
}
