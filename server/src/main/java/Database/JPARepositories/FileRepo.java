package Database.JPARepositories;

import Database.JPAEntities.CoreEntities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepo extends JpaRepository<File, String> {

    // Retrieves a list of files belonging to a chat
    List<File> findByMessageChatChatId(String chatId);
}
