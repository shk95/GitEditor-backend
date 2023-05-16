package com.shk95.giteditor.domain.model.provider;

import com.shk95.giteditor.domain.common.constants.ProviderType;
import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
	name = "service_provider_info",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "prv_user_email")})
@Entity
public class Provider extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prv_seq")
	private Long providerSeq;

	@Enumerated(EnumType.STRING)
	@Column(name = "prv_typ", nullable = false)
	private ProviderType providerType;

	@Column(name = "prv_user_email", nullable = false)
	private String providerEmail;

	@Column(name = "prv_user_id")
	private String providerUserId;

	@Column(name = "prv_user_name")
	private String providerUserName;

	@Column(name = "prv_access_token")
	private String accessToken;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_seq")
	private User user;
}
