package com.skai.template.rabbit;

import com.kenshoo.kjobster.api.JobsterApi;
import com.kenshoo.kjobster.rabbit.producer.JobMessage;
import com.kenshoo.kjobster.rabbit.producer.RabbitProducer;
import com.skai.template.Application;
import com.skai.template.config.rabbit.RabbitConfig;
import com.skai.template.rabbit.messagehandler.ExampleMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = Application.class)
class KjobsterConsumerIntegrationTest {

    private static final String TEST_MESSAGE = "test message";

    @MockBean
    public ExampleMessageHandler exampleMessageHandler;
    @Autowired
    private JobsterApi jobsterApi;
    @Autowired
    private BrokerDetailsFactory brokerDetailsFactory;
    @Autowired
    private RabbitConfig rabbitConfig;

    private RabbitProducer producer;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        // change queue name to prevent conflict with running app in docker during test execution
        registry.add("rabbit.queueName", () -> "example-test-queue");
    }

    @BeforeEach
    public void before() throws Exception {
        this.producer = jobsterApi.getProducer(brokerDetailsFactory.getBrokerDetails(rabbitConfig),
                Optional.of(Duration.ofMinutes(1).toMillis()));
    }

    @Test
    void publishMessageAndVerifyItConsumed() throws Exception {
        JobMessage jobMessage = JobMessage.builder().withMessageBody(TEST_MESSAGE).build();
        producer.publish(jobMessage, rabbitConfig.getBindingKey());
        verify(exampleMessageHandler, timeout(Duration.ofSeconds(10).toMillis())
                .times(1)).handleMessage(eq(TEST_MESSAGE), any(Optional.class));
    }
}
