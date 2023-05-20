package com.shk95.giteditor.config;

public final class ConstantFields {//TODO: 설정값 상수 관리

	//TODO: oauthService: 상수 관리

	public static final String REDIRECT_LOGIN_PATH = "http://localhost:4000/login";
	public static final String REDIRECT_SIGNUP_OAUTH_PATH = "http://localhost:4000/signup/oauth";
	public static final String REDIRECT_SIGNUP_OAUTH_ID = "oauth2_signup";
	public static final int REDIRECT_SIGNUP_OAUTH_EXPIRE = 10 * 60;// 10분


	public static final class OAuthService {
		public static final String PROVIDER_ACCESS_TOKEN = "PROVIDER_ACCESS_TOKEN";// 추가 제공
	}

	public static final class OAuthRepo {
		public static final String OAUTH_DEFAULT_REDIRECT = "http://localhost:4000/oauth/redirect";//TODO: oauth default redirect url 관리
		public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
		public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
		public static final String REFRESH_TOKEN = "refresh_token";
		public static final int COOKIE_EXPIRE_SECONDS = 180;
	}

	public static final class Jwt {
		public static final String AUTHORIZATION_HEADER = "Authorization";
		public static final String AUTHORITIES_KEY = "auth";
		public static final String BEARER_TYPE = "Bearer";
		public static final String TYPE_ACCESS = "access";
		public static final String TYPE_REFRESH = "refresh";

		public static final class ExpireTime {
			public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;               //30분
			public static final long REFRESH_TOKEN_EXPIRE_TIME = 3 * 24 * 60 * 60 * 1000L;     //3일
			public static final long REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS = REFRESH_TOKEN_EXPIRE_TIME / 1000L;
		}
	}
}
