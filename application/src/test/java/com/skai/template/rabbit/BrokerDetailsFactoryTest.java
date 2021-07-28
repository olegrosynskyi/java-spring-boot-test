package com.skai.template.rabbit;

import com.kenshoo.kjobster.api.conf.BrokerDetails;
import com.skai.template.config.rabbit.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BrokerDetailsFactoryTest {

    @InjectMocks
    public BrokerDetailsFactory factory;
    private RabbitConfig rabbitConfig;

    @BeforeEach
    public void init() {
        this.rabbitConfig = new RabbitConfig();
        rabbitConfig.setExchange("exchange");
        rabbitConfig.setBindingKey("bindingKey");
        rabbitConfig.setHost("host");
        rabbitConfig.setPassword("password");
        rabbitConfig.setPort(8080);
        rabbitConfig.setQueueName("queue");
        rabbitConfig.setUsername("user");

    }

    @Test
    public void verifyFactoryCreatesCorrectBrokerDetails() {
        BrokerDetails brokerDetails = factory.getBrokerDetails(rabbitConfig);
        assertEquals(rabbitConfig.getHost(), brokerDetails.getBrokerConnectionDetails().getHost());
        assertEquals(rabbitConfig.getPort(), brokerDetails.getBrokerConnectionDetails().getPort());

        assertEquals(rabbitConfig.getBindingKey(), brokerDetails.getBrokerTopologyDetails().getBindingKey());
        assertEquals(rabbitConfig.getQueueName(), brokerDetails.getBrokerTopologyDetails().getQueueName());
        assertEquals(rabbitConfig.getExchange(), brokerDetails.getBrokerTopologyDetails().getTopic());

        assertEquals(rabbitConfig.getUsername(), brokerDetails.getBrokerCredentials().getUserName());
        assertEquals(rabbitConfig.getPassword(), brokerDetails.getBrokerCredentials().getPassword());
    }

}
