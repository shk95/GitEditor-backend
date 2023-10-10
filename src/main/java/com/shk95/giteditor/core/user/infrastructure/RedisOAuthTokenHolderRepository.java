package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;
import org.springframework.data.repository.CrudRepository;

public interface RedisOAuthTokenHolderRepository extends CrudRepository<ProviderLoginInfo, String> {

}
