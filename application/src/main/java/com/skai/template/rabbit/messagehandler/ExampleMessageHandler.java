package com.skai.template.rabbit.messagehandler;

import com.kenshoo.kjobster.api.MessageAction;
import com.kenshoo.kjobster.api.MessageHandler;
import com.kenshoo.kjobster.api.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class ExampleMessageHandler implements MessageHandler {

    @Override
    public MessageResponse handleMessage(String message, Optional<String> jobName) {
        log.info("Received message : {}", message);
        return MessageResponse.builder()
                .withMessageAction(MessageAction.ACKNOWLEDGE)
                .build();
    }
}
