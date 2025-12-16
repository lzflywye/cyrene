package com.example.user.entity;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserProfile extends PanacheEntityBase {

    @Id
    @Column(name = "user_id")
    public String userId;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "display_name")
    public String displayName;

    @Column(name = "is_email_synced")
    public boolean isEmailSynced;

    @Column(name = "updated_at")
    public Instant updatedAt;

    public static UserProfile findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }
}
