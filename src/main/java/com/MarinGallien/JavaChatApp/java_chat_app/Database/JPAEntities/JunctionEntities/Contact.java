package com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.JunctionEntities;

import com.MarinGallien.JavaChatApp.java_chat_app.Database.JPAEntities.CoreEntities.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact {
    // Columns

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_user_id", nullable = false)
    private User contactUser;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    // Constructors
    public Contact() {}

    public Contact(User user, User contactUser) {
        this.user = user;
        this.contactUser = contactUser;
    }

    // Getters
    public String getId() {return id;}
    public User getUser() {return user;}
    public User getContactUser() {return contactUser;}
    public LocalDateTime getAddedAt() {return addedAt;}


    // Setters
    public void setId(String id) {this.id = id;}
    public void setUser(User user) {this.user = user;}
    public void setContactUser(User contactUser) {this.contactUser = contactUser;}
    public void setAddedAt(LocalDateTime addedAt) {this.addedAt = addedAt;}


    // Helper methods
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return id != null ? id.equals(contact.id) : contact.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
