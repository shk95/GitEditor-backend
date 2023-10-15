package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.application.service.dto.UserDto;

import java.util.List;

public interface FindUserUseCase {

	List<UserDto> getUserListByUsername(String username); // 서비스 사용자 이름

}
