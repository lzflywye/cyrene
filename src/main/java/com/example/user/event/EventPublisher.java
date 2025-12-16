package com.example.user.event;

public interface EventPublisher {
    void publishEmailUpdate(String userId, String newEmail);
}
