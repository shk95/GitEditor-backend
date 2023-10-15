package com.shk95.giteditor.common.security.handler;

import com.shk95.giteditor.common.exception.OAuthUserNotRegisteredException;
import com.shk95.giteditor.common.model.AbstractOAuth2UserInfo;
import com.shk95.giteditor.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.common.utils.web.CookieUtil;
import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.core.auth.application.port.out.LoadUserPort;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.application.port.out.GithubTokenHolderPort;
import com.shk95.giteditor.core.user.application.port.out.OAuthTokenHolderPort;
import com.shk95.giteditor.core.user.application.port.out.ProviderRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;
import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;
import com.shk95.giteditor.core.user.domain.user.UserId;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.shk95.giteditor.config.Constants.*;
import static com.shk95.giteditor.config.Constants.OAuthRepo.OAUTH_DEFAULT_REDIRECT;
import static com.shk95.giteditor.config.Constants.OAuthRepo.OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final ApplicationProperties properties;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
	private final OAuthTokenHolderPort oAuthTokenHolderPort;
	private final GithubTokenHolderPort githubTokenHolderPort;
	private final UserCrudRepositoryPort userCrudRepositoryPort;
	private final ProviderRepositoryPort providerRepositoryPort;
	private final LoadUserPort loadUserPort;

	/*
			기능 : 회원가입 추가정보를 받도록 회원가입 페이지로 리디렉션 & oAuth 인증 쿠키 유지 & oAuth 인증시 가입 정보를 넘겨줌

			oAuth 로그인 성공후 가입된 사용자의 조회 실패시 -> 사용자에게 로그인 페이지로 리디렉션 시켜서 추가 로그인 받는다
			-> onAuthenticationFailure 에서 oAuth 로그인후 일정시간내에 회원가입하지 않을시 쿠키 소멸로 인하여 실패(회원가입 재 진행)
	*/

	@Transactional
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
		, AuthenticationException exception) throws IOException, ServletException {
		CookieUtil.deleteCookie(request, response, REDIRECT_SIGNUP_OAUTH_ID);

		String redirectUrl;
		if (exception instanceof OAuthUserNotRegisteredException) {
			AbstractOAuth2UserInfo userInfo = ((OAuthUserNotRegisteredException) exception).getOAuth2UserInfo();

			Optional<Cookie> oAuthLoginInfo = CookieUtil.getCookie(request, ADD_OAUTH_SERVICE_USER_INFO);
			if (oAuthLoginInfo.isPresent()) {
				// 로그인한 사용자의 서비스 추가
				this.addOAuthService(userInfo, oAuthLoginInfo.get());
				CookieUtil.deleteCookie(request, response, ADD_OAUTH_SERVICE_USER_INFO);

				redirectUrl = UriComponentsBuilder
					.fromUriString(properties.getFrontPageUrl() + OAUTH_DEFAULT_REDIRECT)
					.queryParam("type", "addGithubService")// 현재 github 추가만 있음. TODO: 로그인후 OAuth 서비스 추가하는기능 개선
					.build().toUriString();
			} else {
				// oAuth 로 가입하는 사용자
				this.signup(userInfo);
				CookieUtil.addCookie(
					response
					, REDIRECT_SIGNUP_OAUTH_ID
					, CookieUtil.serialize(userInfo.getId())
					, REDIRECT_SIGNUP_OAUTH_EXPIRE
				);
				redirectUrl = UriComponentsBuilder
					.fromUriString(properties.getFrontPageUrl() + REDIRECT_SIGNUP_OAUTH_PATH)
					.build().toUriString();
			}
		} else {// OAuth 로그인 실패
			exception.printStackTrace();
			String frontendHost = CookieUtil.getCookie(request, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue)
				.orElse(("/"));
			redirectUrl = UriComponentsBuilder
				.fromUriString(frontendHost)
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

		ProviderLoginInfo loginInfo =
			ProviderLoginInfo.builder()
				.id(userInfo.getId())
				.providerType(userInfo.getProviderType().name())
				.loginId(userInfo.getLoginId())
				.email(userInfo.getEmail())
				.name(userInfo.getName())
				.imgUrl(userInfo.getImageUrl()).build();
		oAuthTokenHolderPort.save(loginInfo);
	}

	private void addOAuthService(final AbstractOAuth2UserInfo userInfo, Cookie cookie) {
		UserId userId = UserId.of(CookieUtil.deserialize(cookie, String.class));

		CustomUserDetails userDetails = loadUserPort.loadUser(userId);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		boolean state = githubTokenHolderPort.findById(userId.get()).isPresent();
		if (state) {
			userCrudRepositoryPort.findUserByUserId(userId)
				.ifPresent(user -> {
					user.activateGithubUsage();
					Provider provider = providerRepositoryPort.save(
						Provider.builder()
							.providerId(new ProviderId(userInfo.getProviderType(), userInfo.getId()))
							.providerLoginId(userInfo.getLoginId())
							.providerEmail(userInfo.getEmail())
							.providerImgUrl(userInfo.getImageUrl())
							.providerUserName(userInfo.getName())
							.accessToken(userInfo.getAccessToken())
							.user(user).build());
				});
			githubTokenHolderPort.deleteById(userId.get());
		}
	}
}
