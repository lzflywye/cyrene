package com.example.infrastructure.sqs;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.service.UserSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@ApplicationScoped
@IfBuildProfile("prod")
public class SqsWorker {

    @Inject
    SqsClient sqsClient;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    UserSyncService userSyncService;

    @ConfigProperty(name = "app.messaging.sqs.queue-url")
    String queueUrl;

    @ConfigProperty(name = "app.messaging.sqs.concurrency")
    int concurrency;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final Logger LOG = Logger.getLogger(SqsWorker.class);

    @Startup
    void init() {
        for (int i = 0; i < concurrency; i++) {
            executor.submit(this::pollLoop);
        }
    }

    private void pollLoop() {
        while (running.get()) {
            try {
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .waitTimeSeconds(20)
                        .maxNumberOfMessages(10)
                        .build();

                var response = sqsClient.receiveMessage(request);

                for (var msg : response.messages()) {
                    process(msg);
                    delete(msg);
                }

                for (var msg : response.messages()) {
                    try {
                        process(msg);
                        delete(msg);
                    } catch (Exception e) {
                        LOG.error("Failed to process message: " + msg.messageId(), e);
                    }
                }

            } catch (Exception e) {
                LOG.error("Polling error", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
    }

    private void process(Message msg) throws Exception {
        EmailUpdateEvent event = Objects.requireNonNull(objectMapper.readValue(msg.body(), EmailUpdateEvent.class));

        userSyncService.syncEmail(event.userId(), event.newEmail());

        LOG.info("Processing complete: " + msg.messageId());
    }

    private void delete(Message msg) {
        sqsClient.deleteMessage(req -> req.queueUrl(queueUrl).receiptHandle(msg.receiptHandle()));
    }

    @PreDestroy
    void stop() {
        running.set(false);
        executor.shutdownNow();
    }
}
