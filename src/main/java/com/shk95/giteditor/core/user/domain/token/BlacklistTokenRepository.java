package com.shk95.giteditor.core.user.domain.token;

import org.springframework.data.repository.CrudRepository;

public interface BlacklistTokenRepository extends CrudRepository<BlacklistToken, String> {
}
