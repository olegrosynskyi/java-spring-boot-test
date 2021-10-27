package io.skai.template.config.rabbit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rabbit")
public class RabbitConfig {

    private String host;
    private int port;
    private String username;
    private String password;
    private String exchange;
    private String queueName;
    private String bindingKey;
    private int numOfListeners;
}
