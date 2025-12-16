package com.example.infrastructure.amqp;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.event.EventPublisher;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@IfBuildProperty(name = "app.messaging.provider", stringValue = "amqp")
public class AmqpEventPublisher implements EventPublisher {

    @Inject
    @Channel("email-update")
    Emitter<EmailUpdateEvent> emitter;

    @Override
    public void publishEmailUpdate(String userId, String newEmail) {
        emitter.send(new EmailUpdateEvent(userId, newEmail));
    }
}
