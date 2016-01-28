package com.oudmaijer.spring.jms.netflix;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;

public class CircuitBreakerAwareMessageListenerContainer extends DefaultMessageListenerContainer implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitBreakerAwareMessageListenerContainer.class);
    private List<HystrixCircuitBreaker> hystrixCircuitBreakers = new ArrayList<>();
    private RateLimiter rateLimiter;
    private List<String> hystrixCommandKeys;
    private double permitsPerSecond = 0.5;

    public CircuitBreakerAwareMessageListenerContainer() {
    }

    @Override
    protected boolean receiveAndExecute(Object invoker, Session session, MessageConsumer consumer) throws JMSException {
        determineHystrixCircuitBreakers();
        if (anyRequestNotAllowed()) {
            rateLimiter.acquire(); // may wait
        }
        return super.receiveAndExecute(invoker, session, consumer);
    }

    protected void determineHystrixCircuitBreakers() {
        if (hystrixCommandKeys == null) {
            return;
        }
        for (String key : hystrixCommandKeys) {
            try {
                HystrixCommandKey hystrixCommandKey = HystrixCommandKey.Factory.asKey(key);
                if (hystrixCommandKey == null) {
                    LOG.warn("HystrixCommand with key={} not found, skipping it!", key);
                } else {
                    HystrixCircuitBreaker instance = HystrixCircuitBreaker.Factory.getInstance(hystrixCommandKey);
                    if (instance == null) {
                        LOG.warn("HystrixCircuitBreaker not found for HystrixCommand with key={} not found, skipping it!", key);
                    } else {
                        hystrixCircuitBreakers.add(instance);
                    }
                }
            } catch (Exception e) {
                LOG.error("An exception occurred determining the HystrixCommand or HystrixCircuitBreaker for HystrixCommand with key={}", key);
            }
        }
    }

    protected boolean anyRequestNotAllowed() {
        return hystrixCircuitBreakers.stream().anyMatch(it -> !it.allowRequest());
    }

    public void setHystrixCommandKeys(List<String> hystrixCommandKeys) {
        this.hystrixCommandKeys = hystrixCommandKeys;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.rateLimiter = RateLimiter.create(permitsPerSecond);

    }
}
