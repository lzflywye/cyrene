package com.example.infrastructure.amqp;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.service.UserSyncService;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class AmqpWorkerTest {

    @Inject
    @Channel("email-update")
    Emitter<EmailUpdateEvent> emitter;

    @InjectMock
    UserSyncService userSyncService;

    @Test
    public void testWorkerConsumesMessage() {
        String userId = "msg-user-1";
        emitter.send(new EmailUpdateEvent(userId, "new@test.com"));

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Mockito.verify(userSyncService, Mockito.times(1))
                    .syncEmail(userId, "new@test.com");
        });
    }
}
