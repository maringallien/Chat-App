package com.MarinGallien.JavaChatApp.Database;

import com.sun.jdi.CharType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "chat_id", nullable = false)
    private String chatId;

    @Column(name = "chat_type", nullable = false)
    private CharType chatType;

    @Column(name = "chat_name", nullable = false)
    private String chatName;

    @Column(name = "creator_id", nullable = false)
    private String creatorId;

    @Column(name = "participant_ids", nullable = false)
    private List<String> participantIds;

}
