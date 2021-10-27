package io.skai.template.rabbit;

import com.kenshoo.kjobster.api.JobsterApi;
import com.kenshoo.kjobster.rabbit.producer.JobMessage;
import com.kenshoo.kjobster.rabbit.producer.RabbitProducer;
import io.skai.template.Application;
import io.skai.template.config.rabbit.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class KjobsterConsumerIntegrationTest {

    private static final String TEST_MESSAGE = "test message";

    @Autowired
    private JobsterApi jobsterApi;
    @Autowired
    private BrokerDetailsFactory brokerDetailsFactory;
    @Autowired
    private RabbitConfig rabbitConfig;
    @Autowired
    private RedisOperations<String, String> redisOperations;

    private RabbitProducer producer;

    @BeforeEach
    public void before() throws Exception {
        this.producer = jobsterApi.getProducer(brokerDetailsFactory.getBrokerDetails(rabbitConfig),
                Optional.of(Duration.ofMinutes(1).toMillis()));
    }

    @Test
    void publishMessageAndVerifyItConsumed() throws Exception {
        String jobName = UUID.randomUUID().toString();
        JobMessage jobMessage = JobMessage.builder().withJobName(Optional.of(jobName)).withMessageBody(TEST_MESSAGE).build();
        producer.publish(jobMessage, rabbitConfig.getBindingKey());
        await().atMost(Duration.ofSeconds(10))
                .until(() -> redisOperations.opsForValue().get(jobName), equalTo(TEST_MESSAGE));
    }
}
