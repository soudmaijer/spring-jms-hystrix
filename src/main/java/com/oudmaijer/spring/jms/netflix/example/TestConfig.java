package com.oudmaijer.spring.jms.netflix.example;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.oudmaijer.spring.jms.netflix.CircuitBreakerAwareJmsListenerContainerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.QueueConnectionFactory;
import java.io.FileNotFoundException;

@Configurable
@EnableJms
@EnableAspectJAutoProxy
@ComponentScan("com.oudmaijer.spring.jms.netflix")
public class TestConfig {

    @Bean
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    @Bean
    public QueueConnectionFactory queueConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    @Bean
    public CircuitBreakerAwareJmsListenerContainerFactory jmsListenerContainerFactory() {
        CircuitBreakerAwareJmsListenerContainerFactory factory = new CircuitBreakerAwareJmsListenerContainerFactory();
        factory.setConnectionFactory(queueConnectionFactory());
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(queueConnectionFactory());
        jmsTemplate.setDefaultDestinationName("myQueue");
        return jmsTemplate;
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);

        JmsTemplate bean = context.getBean(JmsTemplate.class);

        for(int i=0; i<1000;i++) {
            bean.convertAndSend("myQueue", "bla"+i);
        }
    }
}
