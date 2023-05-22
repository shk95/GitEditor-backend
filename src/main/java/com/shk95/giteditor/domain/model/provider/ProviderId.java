package com.shk95.giteditor.domain.model.provider;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Getter
@AllArgsConstructor
@Embeddable
public class ProviderId implements Serializable {

	@Enumerated(EnumType.STRING)
	@Column(name = "prv_type")
	private ProviderType providerType;

	@Column(name = "prv_user_id", length = 100)
	private String providerUserId;// 고유값으로 제공되는 아이디. Provider.providerLoginId 와 같기도함.

	protected ProviderId() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProviderId that = (ProviderId) o;
		return providerType == that.providerType && Objects.equals(providerUserId, that.providerUserId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(providerType, providerUserId);
	}
}
