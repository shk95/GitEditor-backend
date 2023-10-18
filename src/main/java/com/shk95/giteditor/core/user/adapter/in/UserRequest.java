package com.shk95.giteditor.core.user.adapter.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


public class UserRequest {

	@Getter
	public static class ForgotPassword {

		@NotBlank(message = "이메일은 필수 입력값 입니다.")
		@Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
			, message = "이메일 형식에 맞지 않습니다.")
		private String defaultEmail;
	}

	@Getter
	public static class UpdatePassword {

		@NotBlank(message = "비밀번호는 필수 입력값 입니다.")
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호는 영문 대소문자 특수문자 숫자를 포함하여 8~16자 이내 입니다.")
		private String password;
	}

	@Getter
	public static class ChangeEmail {

		@NotBlank(message = "이메일은 필수 입력값 입니다.")
		@Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
			, message = "이메일 형식에 맞지 않습니다.")
		private String defaultEmail;
	}

	@Getter
	public static class Profile {

		@Pattern(regexp = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
			, message = "이메일 형식에 맞지 않습니다.")
		private String newEmail;

		@Pattern(regexp = "^[A-Za-z]{1,30}$"
			, message = "닉네임은 영문만 사용하여 1~30 자 이내 입니다.")
		private String newUsername;

		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
			, message = "비밀번호는 영문 대소문자 특수문자 숫자를 포함하여 8~16자 이내 입니다.")
		private String newPassword;
	}

	@Getter
	public static class OpenAI {

		@NotBlank
		private String accessToken;
	}

	@Getter
	public static class Discord {

		private String discordId;
	}

}
