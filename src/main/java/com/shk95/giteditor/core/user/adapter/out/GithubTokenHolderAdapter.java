package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.user.application.port.out.GithubTokenHolderPort;
import com.shk95.giteditor.core.user.domain.user.GithubService;
import com.shk95.giteditor.core.user.infrastructure.RedisGithubTokenHolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GithubTokenHolderAdapter implements GithubTokenHolderPort {

	private final RedisGithubTokenHolderRepository redisGithubTokenHolderRepository;

	@Override
	public GithubService save(GithubService githubService) {
		return redisGithubTokenHolderRepository.save(githubService);
	}

	@Override
	public Optional<GithubService> findById(String id) {
		return redisGithubTokenHolderRepository.findById(id);
	}

	@Override
	public void deleteById(String id) {
		redisGithubTokenHolderRepository.deleteById(id);
	}
}
