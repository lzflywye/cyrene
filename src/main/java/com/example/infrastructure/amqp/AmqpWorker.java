package com.example.infrastructure.amqp;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.example.user.event.EmailUpdateEvent;
import com.example.user.service.UserSyncService;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@UnlessBuildProfile("prod")
public class AmqpWorker {

    @Inject
    UserSyncService userSyncService;

    @Incoming("email-update")
    @Blocking
    public void process(EmailUpdateEvent event) {
        userSyncService.syncEmail(event.userId(), event.newEmail());
    }
}
