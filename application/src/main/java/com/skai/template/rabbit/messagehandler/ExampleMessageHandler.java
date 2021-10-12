package com.skai.template.rabbit.messagehandler;

import com.kenshoo.kjobster.api.MessageAction;
import com.kenshoo.kjobster.api.MessageHandler;
import com.kenshoo.kjobster.api.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleMessageHandler implements MessageHandler {

    private final RedisOperations<String, String> redisOperations;

    @Override
    public MessageResponse handleMessage(String message, Optional<String> jobName) {
        log.info("Received message : {}, jobName : {}", message, jobName.orElse(null));
        jobName.ifPresent(key -> redisOperations.opsForValue().set(key, message));
        return MessageResponse.builder()
                .withMessageAction(MessageAction.ACKNOWLEDGE)
                .build();
    }
}
