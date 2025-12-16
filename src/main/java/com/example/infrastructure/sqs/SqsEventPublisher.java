package com.example.infrastructure.sqs;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.event.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ApplicationScoped
@IfBuildProperty(name = "app.messaging.provider", stringValue = "sqs")
public class SqsEventPublisher implements EventPublisher {

    @Inject
    SqsClient sqsClient;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "app.messaging.sqs.queue-url")
    String queueUrl;

    @Override
    public void publishEmailUpdate(String userId, String newEmail) {
        try {
            EmailUpdateEvent event = new EmailUpdateEvent(userId, newEmail);
            String jsonBody = objectMapper.writeValueAsString(event);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonBody)
                    .build();

            sqsClient.sendMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SQS message", e);
        }
    }
}
