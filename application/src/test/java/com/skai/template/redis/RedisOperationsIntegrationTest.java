package com.skai.template.redis;

import com.skai.template.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
public class RedisOperationsIntegrationTest {

    private static final String MEMBER1 = "member1";
    private static final String MEMBER2 = "member2";

    @Autowired
    private RedisOperations<String, String> redisOperations;

    @Test
    public void testSetOperations() {
        String key = "testSet-" + UUID.randomUUID();
        redisOperations.opsForSet().add(key, MEMBER1, MEMBER2);
        redisOperations.expire(key, Duration.ofMinutes(1));

        assertEquals(redisOperations.opsForSet().size(key), 2);
        assertEquals(redisOperations.opsForSet().members(key), Set.of(MEMBER1, MEMBER2));
    }

    @Test
    public void testHashOperations() {
        String key = "testHashKey-" + UUID.randomUUID();
        String hashKey = "testHash";
        redisOperations.opsForHash().put(key, hashKey, MEMBER1);
        redisOperations.expire(key, Duration.ofMinutes(1));

        assertEquals(redisOperations.opsForHash().get(key, hashKey), MEMBER1);
    }
}
