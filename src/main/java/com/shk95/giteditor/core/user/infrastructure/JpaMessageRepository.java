package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.domain.message.Message;
import com.shk95.giteditor.core.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaMessageRepository extends JpaRepository<Message, Long> {

	int countByRecipientAndRead(User me, boolean read);

	List<Message> findByRecipientAndReadOrderByTimestampAsc(User recipient, boolean read);

	List<Message> findBySender(User sender);

	List<Message> findBySenderAndRecipient(User sender, User recipient);

}
