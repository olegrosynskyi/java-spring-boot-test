package io.skai.template.rabbit;

import com.kenshoo.kjobster.api.conf.BrokerConnectionDetails;
import com.kenshoo.kjobster.api.conf.BrokerCredentials;
import com.kenshoo.kjobster.api.conf.BrokerDetails;
import com.kenshoo.kjobster.api.conf.BrokerTopologyDetails;
import io.skai.template.config.rabbit.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrokerDetailsFactory {

    public BrokerDetails getBrokerDetails(RabbitConfig rabbitConfig) {
        return BrokerDetails.builder()
                .withBrokerConnectionDetails(getBrokerConnectionDetails(rabbitConfig))
                .withBrokerCredentials(getBrokerCredentials(rabbitConfig))
                .withBrokerTopologyDetails(getBrokerTopologyDetails(rabbitConfig))
                .build();
    }

    private BrokerConnectionDetails getBrokerConnectionDetails(RabbitConfig brokerConfig) {
        return new BrokerConnectionDetails(brokerConfig.getHost(), brokerConfig.getPort());
    }

    private BrokerCredentials getBrokerCredentials(RabbitConfig brokerConfig) {
        return new BrokerCredentials(brokerConfig.getUsername(), brokerConfig.getPassword());
    }

    private BrokerTopologyDetails getBrokerTopologyDetails(RabbitConfig queue) {
        return BrokerTopologyDetails.builder()
                .withTopic(queue.getExchange())
                .withQueueName(queue.getQueueName())
                .withBindingKey(queue.getBindingKey())
                .build();
    }
}
