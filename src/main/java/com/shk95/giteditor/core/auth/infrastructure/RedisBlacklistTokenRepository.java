package com.shk95.giteditor.core.auth.infrastructure;

import com.shk95.giteditor.core.auth.domain.BlacklistToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisBlacklistTokenRepository extends CrudRepository<BlacklistToken, String> {

}
