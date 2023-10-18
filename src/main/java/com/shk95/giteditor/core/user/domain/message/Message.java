package com.shk95.giteditor.core.user.domain.message;

import com.shk95.giteditor.core.user.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Table(name = "message")
@Entity
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "sender_prv_typ", referencedColumnName = "user_prv_typ"),
		@JoinColumn(name = "sender_login_id", referencedColumnName = "user_login_id")
	})
	private User sender;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "recipient_prv_typ", referencedColumnName = "user_prv_typ"),
		@JoinColumn(name = "recipient_login_id", referencedColumnName = "user_login_id")
	})
	private User recipient;

	@Column(name = "content", updatable = false)
	private String content;

	@Column(name = "msg_read")
	private boolean read;

	@CreationTimestamp
	@Column(name = "timestamp", updatable = false)
	private LocalDateTime timestamp;

	public Message() {
	}

	@Builder
	public Message(Long id, User sender, User recipient, String content, LocalDateTime timestamp) {
//		this.id = id;
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
//		this.timestamp = timestamp;
	}

	public Message markAsRead() {
		this.read = true;
		return this;
	}
}
