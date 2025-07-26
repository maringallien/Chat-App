package com.MarinGallien.JavaChatApp.Database.JPAEntities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
public class Session {
    // Columns:

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Column(name = "session_token", nullable = false, unique = true)
    private String sessionToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;


    // Constructors

    public Session() {}

    public Session(User user, String sessionToken, LocalDateTime expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.expiresAt = expiresAt;
    }


    // Getters
    public String getSessionId() {return sessionId;}
    public User getUser() {return user;}
    public String getSessionToken() {return sessionToken;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getExpiresAt() {return expiresAt;}
    public Boolean getActive() {return isActive;}


    // Setters
    public void setSessionId(String sessionId) {this.sessionId = sessionId;}
    public void setUser(User user) {this.user = user;}
    public void setSessionToken(String sessionToken) {this.sessionToken = sessionToken;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setExpiresAt(LocalDateTime expiresAt) {this.expiresAt = expiresAt;}
    public void setActive(Boolean active) {isActive = active;}


    // Helper methods

    // Check if session is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // Check if session is valid (active and not expired)
    public boolean isValid() {
        return isActive && !isExpired();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Session session = (Session) obj;
        return sessionId != null ? sessionId.equals(session.sessionId) : session.sessionId == null;
    }

    @Override
    public int hashCode() {
        return sessionId != null ? sessionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + (user != null ? user.getUserId() : null) + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
