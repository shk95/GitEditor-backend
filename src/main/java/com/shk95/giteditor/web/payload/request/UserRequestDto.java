package com.shk95.giteditor.web.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UserRequestDto {

	@Getter
	@Setter
	public static class SignUp {

		@NotEmpty
		@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{4,11}$", message = "아이디 형식에 맞지 않습니다.")
		private String userId;

		@NotEmpty(message = "이메일은 필수 입력값 입니다.")
		@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
		private String defaultEmail;

		@NotEmpty(message = "비밀번호는 필수 입력값 입니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
		private String password;

		@NotEmpty(message = "닉네임은 영문만 입력해 주세요.")
		@Pattern(regexp = "^[A-Za-z]{1,20}$")
		private String username;
	}

	@Getter
	@Setter
	public static class Login {
		@NotEmpty(message = "아이디는 필수 입력값입니다.")
		@Pattern(regexp = "^([a-zA-Z][a-zA-Z0-9_]{4,11})|([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6})$"
			, message = "아이디 형식에 맞지 않습니다.")
		private String userId;// id or email

		@NotEmpty(message = "비밀번호는 필수 입력값입니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호 형식에 맞지 않습니다.")
		private String password;
	}
}
