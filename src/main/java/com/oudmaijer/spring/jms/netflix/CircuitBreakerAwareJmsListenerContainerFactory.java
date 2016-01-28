package com.oudmaijer.spring.jms.netflix;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.Arrays;

public class CircuitBreakerAwareJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory {

    @Override
    public DefaultMessageListenerContainer createContainerInstance() {
        return new CircuitBreakerAwareMessageListenerContainer();
    }

    @Override
    public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint) {
        DefaultMessageListenerContainer listenerContainer = super.createListenerContainer(endpoint);

        if (endpoint instanceof MethodJmsListenerEndpoint && listenerContainer instanceof CircuitBreakerAwareMessageListenerContainer) {
            MethodJmsListenerEndpoint mle = (MethodJmsListenerEndpoint) endpoint;
            CircuitBreakerAwareJmsListener declaredAnnotation = mle.getMethod().getDeclaredAnnotation(CircuitBreakerAwareJmsListener.class);
            if (declaredAnnotation != null) {
                String[] strings = declaredAnnotation.hystrixCommandKeys();
                if (strings != null) {
                    CircuitBreakerAwareMessageListenerContainer container = (CircuitBreakerAwareMessageListenerContainer) listenerContainer;
                    container.setHystrixCommandKeys(Arrays.asList(strings));
                    container.setPermitsPerSecond(declaredAnnotation.permitsPerSecond());
                }
            }
        }
        return listenerContainer;
    }
}
