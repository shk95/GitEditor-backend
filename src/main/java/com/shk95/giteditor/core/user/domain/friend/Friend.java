package com.shk95.giteditor.core.user.domain.friend;

import com.shk95.giteditor.core.user.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@DynamicInsert
@Getter
@Table(name = "friend")
@Entity
public class Friend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "requester_prv_typ", referencedColumnName = "user_prv_typ"),
		@JoinColumn(name = "requester_login_id", referencedColumnName = "user_login_id")
	})
	private User requester;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "sender_prv_typ", referencedColumnName = "user_prv_typ"),
		@JoinColumn(name = "sender_login_id", referencedColumnName = "user_login_id")
	})
	private User addressee;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private FriendshipStatus status;


	public Friend() {
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public void setAddressee(User addressee) {
		this.addressee = addressee;
	}

	public void setStatus(FriendshipStatus status) {
		this.status = status;
	}
}
