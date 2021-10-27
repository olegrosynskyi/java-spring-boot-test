package io.skai.template.rabbit.consumer;

import com.kenshoo.kjobster.api.JobsterApi;
import io.skai.template.config.rabbit.RabbitConfig;
import io.skai.template.rabbit.BrokerDetailsFactory;
import io.skai.template.rabbit.messagehandler.ExampleMessageHandler;
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
