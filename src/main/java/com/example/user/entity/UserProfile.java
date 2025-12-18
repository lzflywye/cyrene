package com.example.user.entity;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class UserProfile extends PanacheEntityBase {

    @Id
    @Column(name = "user_id")
    public String userId;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "display_name")
    public String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public UserStatus status;

    @Column(name = "is_email_synced")
    public boolean isEmailSynced;

    @Column(name = "created_at")
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = Instant.now();
        }
        if (this.status == null) {
            this.status = UserStatus.PENDING;
        }
    }

    public static UserProfile findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }
}
