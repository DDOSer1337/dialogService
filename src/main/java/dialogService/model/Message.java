package dialogService.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.json.JSONPropertyName;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "message_text", nullable = false)
    private String messageText;

    @JsonProperty("conversationPartner1")
    @Column(name = "author", nullable = false, length = 50)
    private UUID author;

    @Column(name = "is_edited", nullable = false)
    private boolean isEdited;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @JsonProperty("conversationPartner2")
    @Column(name = "recipient", nullable = false)
    private UUID recipient;

    @Column(name = "dialogs_id")
    private UUID dialog;

    public Message(String messageText, UUID author, boolean isEdited,
                   boolean isRead, boolean isDeleted, LocalDateTime time, UUID dialog, UUID recipient) {
        this.messageText = messageText;
        this.author = author;
        this.isEdited = isEdited;
        this.isRead = isRead;
        this.isDeleted = isDeleted;
        this.time = time;
        this.dialog = dialog;
        this.recipient = recipient;
    }
}
