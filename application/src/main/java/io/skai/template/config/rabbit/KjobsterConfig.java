package io.skai.template.config.rabbit;

import com.kenshoo.kjobster.api.JobsterApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KjobsterConfig {

    @Bean
    public JobsterApi jobsterApi() {
        return new JobsterApi();
    }
}
