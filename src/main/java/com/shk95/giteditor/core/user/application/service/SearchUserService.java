package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.core.user.application.port.in.SearchUserUseCase;
import com.shk95.giteditor.core.user.application.port.out.FetchUserProjectionPort;
import com.shk95.giteditor.core.user.application.port.out.projection.SimpleUserProjection;
import com.shk95.giteditor.core.user.application.service.dto.UserDto;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchUserService implements SearchUserUseCase {

	private final FetchUserProjectionPort fetchUserProjectionPort;

	@Transactional(readOnly = true)
	@Override
	public List<UserDto> getUserListLikeUsername(String username) {
		List<SimpleUserProjection> list = fetchUserProjectionPort.fetchUserListLikeUsername(username);
		return list.stream()
			.map(l ->
				new UserDto(
					new UserId(l.getProviderType(), l.getUserLoginId()).get(),
					l.getUsername(),
					l.getProfileImageUrl(),
					l.getDefaultEmail()))
			.collect(Collectors.toList());
	}
}
