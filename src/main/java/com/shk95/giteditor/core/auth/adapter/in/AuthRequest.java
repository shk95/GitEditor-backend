package com.shk95.giteditor.core.auth.adapter.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class AuthRequest {

	public static class Signup {

		@Getter
		@Setter
		public static class Default {

			@NotEmpty(message = "아이디는 필수 입력값 입니다.")
			@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{4,25}$", message = "아이디는 영문 대소문자와 숫자를 사용하여 4~25자 이어야 합니다.")
			private String userId;

			@NotEmpty(message = "이메일은 필수 입력값 입니다.")
			@Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
				, message = "이메일 형식에 맞지 않습니다.")
			private String defaultEmail;

			@NotEmpty(message = "비밀번호는 필수 입력값 입니다.")
			@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
				, message = "비밀번호는 영문 대소문자 특수문자 숫자를 포함하여 8~16자 이내 입니다.")
			private String password;

			@NotEmpty(message = "이름은 필수 입력값 입니다.")
			@Pattern(regexp = "^[A-Za-z]{1,30}$", message = "닉네임은 영문만 사용하여 1~30 자 이내 입니다.")
			private String username;
		}

		@Getter
		public static class OAuth {

			public OAuth() {
			}

			@Builder
			public OAuth(String username) {
				this.username = username;
			}
/*	@NotEmpty(message.http = "아이디는 필수 입력값 입니다.")
			@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{4,25}$")
			private String userId;*/

			@NotEmpty(message = "이름은 필수 입력값 입니디.")
			@Pattern(regexp = "^[A-Za-z]{1,30}$")
			private String username;
		}
	}

	@Getter
	@Setter
	public static class Login {

		@NotEmpty(message = "아이디는 필수 입력값 입니다.")
		@Pattern(regexp = "^([a-zA-Z][a-zA-Z0-9_]{4,25})|((([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,})))$"
			, message = "아이디 또는 이메일을 입력해주세요.")
		private String userId;// id or email

		@NotEmpty(message = "비밀번호는 필수 입력값 입니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "올바르지 않은 형식의 비밀번호 입니다.")
		private String password;
	}
}
