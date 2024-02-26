package dialogService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dialogs")
@NoArgsConstructor
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "conversation_partner_1", nullable = false, length = 32)
    private UUID conversationPartner1;

    @Column(name = "conversation_partner_2", nullable = false, length = 32)
    private UUID conversationPartner2;

    @Column(name = "unread_count", nullable = false)
    private int unreadCount;

    @OneToMany(mappedBy = "dialog", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Message> messages;

}