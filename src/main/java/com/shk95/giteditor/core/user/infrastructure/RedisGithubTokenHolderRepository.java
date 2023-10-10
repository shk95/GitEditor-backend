package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.domain.user.GithubService;
import org.springframework.data.repository.CrudRepository;


public interface RedisGithubTokenHolderRepository extends CrudRepository<GithubService, String> {

}
