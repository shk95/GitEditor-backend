package com.shk95.giteditor.domain.common.security.handler;

import com.shk95.giteditor.domain.common.exception.OAuthUserNotRegisteredException;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;
import com.shk95.giteditor.domain.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.domain.model.provider.*;
import com.shk95.giteditor.domain.model.user.GithubServiceRepository;
import com.shk95.giteditor.domain.model.user.UserId;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.shk95.giteditor.config.ConstantFields.*;
import static com.shk95.giteditor.config.ConstantFields.OAuthRepo.OAUTH_DEFAULT_REDIRECT;
import static com.shk95.giteditor.config.ConstantFields.OAuthRepo.OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
	private final ProviderLoginInfoRepository providerLoginInfoRepository;
	private final GithubServiceRepository githubServiceRepository;
	private final UserRepository userRepository;
	private final ProviderRepository providerRepository;

	/*
			기능 : 회원가입 추가정보를 받도록 회원가입 페이지로 리디렉션 & oAuth 인증 쿠키 유지 & oAuth 인증시 가입 정보를 넘겨줌

			oAuth 로그인 성공후 가입된 사용자의 조회 실패시 -> 사용자에게 로그인 페이지로 리디렉션 시켜서 추가 로그인 받는다
			-> onAuthenticationFailure 에서 oAuth 로그인후 일정시간내에 회원가입하지 않을시 쿠키 소멸로 인하여 실패(회원가입 재 진행)
	*/

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
		, AuthenticationException exception) throws IOException, ServletException {
		CookieUtil.deleteCookie(request, response, REDIRECT_SIGNUP_OAUTH_ID);

		String redirectUrl;
		if (exception instanceof OAuthUserNotRegisteredException) {
			AbstractOAuth2UserInfo userInfo = ((OAuthUserNotRegisteredException) exception).getOAuth2UserInfo();

			Optional<Cookie> addServiceCookie = CookieUtil.getCookie(request, ADD_OAUTH_SERVICE_USER_INFO);
			if (addServiceCookie.isPresent()) {
				// 로그인한 사용자의 서비스 추가
				this.addOAuthService(userInfo, addServiceCookie.get());
				CookieUtil.deleteCookie(request, response, ADD_OAUTH_SERVICE_USER_INFO);
				redirectUrl = OAUTH_DEFAULT_REDIRECT;
			} else {
				// oAuth 로 가입한 사용자
				this.signup(userInfo);
				CookieUtil.addCookie(
					response
					, REDIRECT_SIGNUP_OAUTH_ID
					, CookieUtil.serialize(userInfo.getId())
					, REDIRECT_SIGNUP_OAUTH_EXPIRE
				);
				redirectUrl = REDIRECT_SIGNUP_OAUTH_PATH;
			}
		} else {
			exception.printStackTrace();
			String frontendHost = CookieUtil.getCookie(request, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue)
				.orElse(("/"));
			redirectUrl = UriComponentsBuilder.fromUriString(frontendHost)
				.queryParam("error", exception.getLocalizedMessage())
				.build().toUriString();
		}
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
		response.setStatus(HttpServletResponse.SC_OK);
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}

	private void signup(AbstractOAuth2UserInfo userInfo) {
		// 회원가입이 필요할경우
		log.info("oAuth 인증 사용자 회원가입 페이지로 리디렉션");

		ProviderLoginInfo loginInfo = new ProviderLoginInfo(
			userInfo.getId(), userInfo.getProviderType().name()
			, userInfo.getLoginId(), userInfo.getEmail()
			, userInfo.getName(), userInfo.getImageUrl());
		providerLoginInfoRepository.save(loginInfo);
	}

	private void addOAuthService(AbstractOAuth2UserInfo userInfo, Cookie cookie) {
		UserId userId = CookieUtil.deserialize(cookie, UserId.class);

		boolean state = githubServiceRepository.findById(userId).isPresent();
		if (state) {
			userRepository.findById(userId)
				.ifPresent(user -> {
					Provider provider = providerRepository.save(Provider.builder()
						.providerId(new ProviderId(userInfo.getProviderType(), userInfo.getId()))
						.providerLoginId(userId.getUserLoginId()).providerEmail(userInfo.getEmail())
						.providerImgUrl(userInfo.getImageUrl()).providerUserName(userInfo.getName())
						.accessToken(userInfo.getAccessToken()).user(user).build());
				});
			githubServiceRepository.deleteById(userId);
		}
	}
}
