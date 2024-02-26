package dialogService.repository;


import dialogService.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, UUID> {


    @Query(value = "Select Count(*) From dialog.messages " +
            "WHERE author = :author and is_read = 'FALSE'", nativeQuery = true)
    Optional<Long> getCountUnreadMessage(UUID author);


    @Query(value = "Select Count(*) From dialog.messages " +
            "WHERE author = :author And dialogs_id = :dialogID and is_read = 'FALSE'", nativeQuery = true)
    Optional<Integer> getCountUnreadMessageInDialog(UUID author, UUID dialogID);


    Page<Message> findByDialog(UUID dialog, Pageable pageable );
}
