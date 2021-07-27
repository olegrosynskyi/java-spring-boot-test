package com.skai.template.rabbit.consumer;

import com.kenshoo.kjobster.api.Consumer;
import com.kenshoo.kjobster.api.JobsterApi;
import com.kenshoo.kjobster.api.MessageHandler;
import com.kenshoo.kjobster.api.conf.ConsumerConfiguration;
import com.skai.template.config.rabbit.RabbitConfig;
import com.skai.template.rabbit.BrokerDetailsFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.SmartLifecycle;

@Slf4j
@RequiredArgsConstructor
public class KjobsterConsumer implements SmartLifecycle, BeanNameAware {

    private final BrokerDetailsFactory factory;
    private final RabbitConfig rabbitConfig;
    private final JobsterApi jobsterApi;
    private final MessageHandler messageHandler;

    private Consumer consumer;
    private boolean isRunning;
    private String name;

    @Override
    public void start() {
        ConsumerConfiguration consumerConfiguration = getConsumerConfiguration();
        consumer = jobsterApi.startConsumer(consumerConfiguration, messageHandler);
        isRunning = true;
        log.info("Consumer : {} started successfully", name);
    }

    @Override
    public void stop() {
        consumer.shutDown();
        isRunning = false;
        log.info("Consumer : {} stopped successfully", name);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    private ConsumerConfiguration getConsumerConfiguration() {
        return ConsumerConfiguration.builder()
                .listeningTo(factory.getBrokerDetails(rabbitConfig))
                .withWorkers(rabbitConfig.getNumOfListeners())
                .build();
    }
}
