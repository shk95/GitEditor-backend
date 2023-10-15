package com.shk95.giteditor.core.user.domain.user;

import com.shk95.giteditor.common.constant.ProviderType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@Embeddable
public class UserId implements Serializable {

	@Enumerated(EnumType.STRING)
	@Column(name = "user_prv_typ", nullable = false, length = 20)
	private ProviderType providerType;

	@Column(name = "user_login_id", nullable = false, length = 50)
	private String userLoginId;

	protected UserId() {
	}

	public static UserId of(String providerTypeAndLoginId) {
		String[] sub = providerTypeAndLoginId.split(",");
		return new UserId(ProviderType.valueOf(sub[0]), sub[1]);
	}

	public String get() {
		return this.providerType.name() + ',' + this.userLoginId;
	}

	@Override
	public String toString() {
		return "UserId{" +
			"userLoginId='" + userLoginId + '\'' +
			", providerType=" + providerType +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserId userId = (UserId) o;
		return Objects.equals(userLoginId, userId.userLoginId) && providerType == userId.providerType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userLoginId, providerType);
	}
}
