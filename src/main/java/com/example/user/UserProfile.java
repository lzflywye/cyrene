package com.example.user;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile extends PanacheEntityBase {

    @Id
    @Column(name = "user_id")
    public String userId;

    @Column(name = "email", unique = true)
    public String email;

    @Column(name = "display_name")
    public String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public UserStatus status = UserStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at")
    public Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public Instant updatedAt;

    public static UserProfile findByUserId(String userId) {
        return find("userId", userId).firstResult();
    }
}
