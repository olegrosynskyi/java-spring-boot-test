package io.skai.template.rabbit.consumer;

import com.kenshoo.kjobster.api.Consumer;
import com.kenshoo.kjobster.api.JobsterApi;
import com.kenshoo.kjobster.api.MessageHandler;
import com.kenshoo.kjobster.api.conf.BrokerDetails;
import com.kenshoo.kjobster.api.conf.ConsumerConfiguration;
import io.skai.template.config.rabbit.RabbitConfig;
import io.skai.template.rabbit.BrokerDetailsFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KjobsterConsumerTest {

    @InjectMocks
    private KjobsterConsumer kjobsterConsumer;
    @Mock
    private BrokerDetailsFactory factory;
    @Mock
    private RabbitConfig rabbitConfig;
    @Mock
    private JobsterApi jobsterApi;
    @Mock
    private MessageHandler messageHandler;

    @Captor
    private ArgumentCaptor<ConsumerConfiguration> argumentCaptor;

    @Test
    public void verifyConsumerInitializationAndLifecycle() {
        assertFalse(kjobsterConsumer.isRunning());
        when(rabbitConfig.getNumOfListeners()).thenReturn(2);

        BrokerDetails brokerDetails = mock(BrokerDetails.class);
        when(factory.getBrokerDetails(rabbitConfig)).thenReturn(brokerDetails);

        Consumer consumer = mock(Consumer.class);
        when(jobsterApi.startConsumer(any(ConsumerConfiguration.class), eq(messageHandler))).thenReturn(consumer);

        kjobsterConsumer.start();

        verify(jobsterApi).startConsumer(argumentCaptor.capture(), eq(messageHandler));
        ConsumerConfiguration consumerConfiguration = argumentCaptor.getValue();
        assertEquals(brokerDetails, consumerConfiguration.getBrokerDetails());
        assertEquals(2, consumerConfiguration.getWorkers());
        assertTrue(kjobsterConsumer.isRunning());

        kjobsterConsumer.stop();
        verify(consumer).shutDown();
        assertFalse(kjobsterConsumer.isRunning());
    }
}
