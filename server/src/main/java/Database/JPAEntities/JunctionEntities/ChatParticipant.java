package Database.JPAEntities.JunctionEntities;

import Database.JPAEntities.CoreEntities.Chat;
import Database.JPAEntities.CoreEntities.User;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participants")
public class ChatParticipant {
    // Columns

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;


    // Constructors

    public ChatParticipant() {}

    public ChatParticipant(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }


    // Getters
    public String getId() {return id;}
    public Chat getChat() {return chat;}
    public User getUser() {return user;}
    public LocalDateTime getJoinedAt() {return joinedAt;}


    // Setters
    public void setId(String id) {this.id = id;}
    public void setChat(Chat chat) {this.chat = chat;}
    public void setUser(User user) {this.user = user;}
    public void setJoinedAt(LocalDateTime joinedAt) {this.joinedAt = joinedAt;}


    // Helper methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChatParticipant that = (ChatParticipant) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
