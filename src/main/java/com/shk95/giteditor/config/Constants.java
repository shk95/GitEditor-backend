package com.shk95.giteditor.config;

import java.util.HashSet;
import java.util.Set;

public final class Constants {

	public static final String REDIRECT_LOGIN_PATH = "/login";
	public static final String REDIRECT_SIGNUP_OAUTH_PATH = "/signup/oauth";
	public static final String REDIRECT_SIGNUP_OAUTH_ID = "oauth2_signup";
	public static final int REDIRECT_SIGNUP_OAUTH_EXPIRE = 10 * 60;// 10분
	public static final int ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION = 10 * 60;
	public static final String ADD_OAUTH_SERVICE_USER_INFO = "service_user_id";

	public static final class OAuthService {

		public static final String PROVIDER_ACCESS_TOKEN = "PROVIDER_ACCESS_TOKEN";// 추가 제공
	}

	public static final class OAuthRepo {

		public static final String OAUTH_DEFAULT_REDIRECT = "/oauth/redirect";//TODO: oauth default redirect url 수정
		public static final String OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
		public static final String OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
		public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
		public static final int OAUTH_COOKIE_EXPIRE_SECONDS = 180;
	}

	public static final class Jwt {

		public static final String AUTHORIZATION_HEADER = "Authorization";
		public static final String AUTHORITIES_KEY = "auth";
		public static final String BEARER_TYPE = "Bearer";
		public static final String JWT_TYPE_ACCESS = "access";
		public static final String JWT_TYPE_REFRESH = "refresh";

		public static final class ExpireTime {

			public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;               //30분
			public static final long REFRESH_TOKEN_EXPIRE_TIME = 3 * 24 * 60 * 60 * 1000L;     //3일
			public static final long REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS = REFRESH_TOKEN_EXPIRE_TIME / 1000L;
		}
	}

	public static final class Thumbnail {

		public static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>();
		public static final int MAX_WIDTH = 420;
		public static final int MAX_HEIGHT = 420;

		static {
			SUPPORTED_EXTENSIONS.add("png");
			SUPPORTED_EXTENSIONS.add("jpg");
			SUPPORTED_EXTENSIONS.add("jpeg");
		}
	}
}
