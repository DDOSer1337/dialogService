package dialogService.repository;

import dialogService.model.Dialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DialogRepository extends CrudRepository<Dialog, UUID> {

    Dialog findByConversationPartner1AndConversationPartner2(UUID ConversationPartner1, UUID ConversationPartner2);

    Page<Dialog> findByConversationPartner1OrAndConversationPartner2(UUID Conversation_partner_1,UUID Conversation_partner_2, Pageable pageable);

    @Modifying
    @Query(value = "UPDATE dialog.messages SET is_read = 'true' " + "WHERE dialogs_id = :dialogID and recipient = :recipient ", nativeQuery = true)
    void changeMessageStatus(UUID dialogID, UUID recipient);
    @Modifying
    @Query(value = "UPDATE dialog.dialogs SET unread_count = 0 " + "WHERE id = :dialogID", nativeQuery = true)
    void changeDialogStatus(UUID dialogID);
}
