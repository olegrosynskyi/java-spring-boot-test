package com.skai.template.rabbit.consumer;

import com.kenshoo.kjobster.api.JobsterApi;
import com.skai.template.config.rabbit.RabbitConfig;
import com.skai.template.rabbit.BrokerDetailsFactory;
import com.skai.template.rabbit.messagehandler.ExampleMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KjobsterConsumerRegistry {

    @Bean(name = "exampleConsumer")
    public KjobsterConsumer exampleConsumer(BrokerDetailsFactory factory, JobsterApi jobsterApi,
                                            ExampleMessageHandler messageHandler,
                                            RabbitConfig rabbitConfig) {
        return new KjobsterConsumer(factory, rabbitConfig, jobsterApi, messageHandler);
    }
}
