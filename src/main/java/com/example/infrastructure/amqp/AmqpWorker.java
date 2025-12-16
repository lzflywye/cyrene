package com.example.infrastructure.amqp;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.service.UserSyncService;

import io.quarkus.arc.properties.IfBuildProperty;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@IfBuildProperty(name = "app.messaging.provider", stringValue = "amqp")
public class AmqpWorker {

    @Inject
    UserSyncService userSyncService;

    @Incoming("email-update")
    @RunOnVirtualThread
    public void process(EmailUpdateEvent event) {
        userSyncService.syncEmail(event.userId(), event.newEmail());
    }
}
