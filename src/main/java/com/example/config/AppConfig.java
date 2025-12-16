package com.example.config;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface AppConfig {

    AwsConfig aws();

    interface AwsConfig {
        String region();
    }

    IdentityConfig identity();

    interface IdentityConfig {
        @WithName("provider")
        IdentityProviderType provider();

        Optional<CognitoConfig> cognito();

        Optional<KeycloakConfig> keycloak();
    }

    enum IdentityProviderType {
        COGNITO, KEYCLOAK
    }

    interface CognitoConfig {
        @WithName("user-pool-id")
        String userPoolId();
    }

    interface KeycloakConfig {
        String realm();
    }

    MessagingConfig messaging();

    interface MessagingConfig {
        @WithName("provider")
        MessagingProviderType provider();

        Optional<SqsConfig> sqs();
    }

    enum MessagingProviderType {
        AMQP, SQS
    }

    interface SqsConfig {
        @WithName("concurrency")
        @WithDefault("3")
        int concurrency();

        @WithName("queue-url")
        String queueUrl();
    }
}
