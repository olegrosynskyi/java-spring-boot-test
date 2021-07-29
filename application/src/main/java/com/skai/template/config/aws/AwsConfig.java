package com.skai.template.config.aws;

import com.kenshoo.aws.credentials.provider.AwsCredentialsProviderConfiguration;
import com.kenshoo.aws.credentials.provider.GenerateAwsCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Bean
    public AwsCredentialsProviderConfiguration awsCredentialsProviderConfiguration(@Value("${aws.accessKey}") String accessKey,
                                                                                   @Value("${aws.secretKey}") String secretKey,
                                                                                   @Value("${aws.useRoleBasedAuth}") boolean useRoleBasedAuth,
                                                                                   @Value("${aws.webTokenFile}") String webTokenFile,
                                                                                   @Value("${aws.roleArnName}") String roleArnName,
                                                                                   @Value("${aws.accessKeyAssumeRole}") String accessKeyAssumeRole,
                                                                                   @Value("${aws.secretKeyAssumeRole}") String secretKeyAssumeRole,
                                                                                   @Value("${aws.roleAppName}") String roleAppName) {
        return new AwsCredentialsProviderConfiguration(useRoleBasedAuth, accessKey, secretKey, webTokenFile,
                roleArnName, accessKeyAssumeRole, secretKeyAssumeRole, roleAppName);
    }

    @Bean
    public GenerateAwsCredentialsProvider awsCredentialsProvider(AwsCredentialsProviderConfiguration configuration) {
        return new GenerateAwsCredentialsProvider(configuration);
    }
}
