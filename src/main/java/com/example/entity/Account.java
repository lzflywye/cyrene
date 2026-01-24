package com.example.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "sub", updatable = false)
    @NotBlank
    private String sub;

    @Column(name = "email", unique = true, nullable = false)
    @NotNull
    private String email;

    @Column(name = "full_name")
    @Nullable
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)

    private AccountStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Account(String sub, String email, String fullName, AccountStatus accountStatus) {
        this.sub = sub;
        this.email = email;
        this.fullName = fullName;
        this.status = Objects.requireNonNullElse(accountStatus, AccountStatus.PENDING);
    }

    protected Account() {
    }

    public String getSub() {
        return sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Optional<String> getFullName() {
        return Optional.ofNullable(fullName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus accountStatus) {
        this.status = accountStatus;
    }
}
