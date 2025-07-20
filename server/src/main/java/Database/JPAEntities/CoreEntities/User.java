package Database.JPAEntities.CoreEntities;

import Enums.OnlineStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {


    // Columns:

    // Create primary key column
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    // Create username column
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    // Create email column
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Create password column
    @NotBlank(message = "Password cannot be blank")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Create dateJoined column
    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false)
    private LocalDateTime dateJoined;

    // Create lastUpdate column - not essential but best practice to keep track of
    @CreationTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    // Create user status column and set to OFFLINE by default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OnlineStatus status = OnlineStatus.OFFLINE;


    // Relationships:

    // User to messages relationship
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> sentMessages = new HashSet<>();

    // User to files relationship
    @OneToMany(mappedBy = "uploader", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> uploadedFiles = new HashSet<>();

    // User to session relationship
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Session session;


    // Constructors
    public User() {}

    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }


    // Getters
    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public LocalDateTime getDateJoined() {
        return dateJoined;
    }
    public LocalDateTime getLastUpdated() {
        return lastUpdate;
    }
    public OnlineStatus getStatus() {
        return status;
    }
    public Set<Message> getSentMessages() {return sentMessages;}
    public Set<File> getUploadedFiles() {
        return uploadedFiles;
    }
    public Session getSession() {
        return session;
    }


    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }
    public void setLastUpdated(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void setStatus(OnlineStatus status) {
        this.status = status;
    }
    public void setSentMessages(Set<Message> sentMessages) {
        this.sentMessages = sentMessages;
    }
    public void setUploadedFiles(Set<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }
    public void setSession(Session session) {
        this.session = session;
    }


    // Utility methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", dateJoined=" + dateJoined +
                '}';
    }
}
