package com.shk95.giteditor.domain.common.security.handler;

import com.shk95.giteditor.domain.common.security.exception.OAuthUserNotRegisteredException;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import com.shk95.giteditor.domain.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfo;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfoRepository;
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

import static com.shk95.giteditor.config.ConstantFields.OAuthRepo.REDIRECT_URI_PARAM_COOKIE_NAME;
import static com.shk95.giteditor.config.ConstantFields.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
	private final ProviderLoginInfoRepository providerLoginInfoRepository;

	/* TODO:
			기능 : 회원가입 추가정보를 받도록 회원가입 페이지로 리디렉션 & oAuth 인증 쿠키 유지 & oAuth 인증시 가입 정보를 넘겨줌

			oAuth 로그인 성공후 가입된 사용자의 조회 실패시 -> 사용자에게 로그인 페이지로 리디렉션 시켜서 추가 로그인 받는다
			-> onAuthenticationFailure 에서 oAuth 로그인후 일정시간내에 회원가입하지 않을시 쿠키 소멸로 인하여 실패(회원가입 재 진행)
	*/

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response
		, AuthenticationException exception) throws IOException, ServletException {
		CookieUtil.deleteCookie(request, response, REDIRECT_SIGNUP_OAUTH_ID);

		String redirectUrl;
		// 회원가입이 필요할경우
		if (exception instanceof OAuthUserNotRegisteredException) {
			log.info("oAuth 인증 사용자 회원가입 페이지로 리디렉션");
			OAuth2UserInfo userInfo = ((OAuthUserNotRegisteredException) exception).getOAuth2UserInfo();
			String providerType = userInfo.getProviderType().name();
			String id = userInfo.getId();
			String loginId = userInfo.getLoginId();
			String email = userInfo.getEmail();
			String name = userInfo.getName();//FIXME: OAuthFailHandler: 빈 문자열인경우 처리

			redirectUrl = REDIRECT_SIGNUP_OAUTH_PATH;

			ProviderLoginInfo loginInfo = new ProviderLoginInfo(id, providerType, loginId, email, name);
			providerLoginInfoRepository.save(loginInfo);

			CookieUtil.addCookie(
				response
				, REDIRECT_SIGNUP_OAUTH_ID
				, CookieUtil.serialize(id)
				, REDIRECT_SIGNUP_OAUTH_EXPIRE
			);
			//TODO: OAuthFailHandler: signup: 쿠키 등의 보안 추가
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			exception.printStackTrace();
			redirectUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue)
				.orElse(("/"));
			redirectUrl = UriComponentsBuilder.fromUriString(redirectUrl)
				.queryParam("error", exception.getLocalizedMessage())
				.build().toUriString();
		}
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
		//TODO: OAuthFailHandler: response 구현
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}
