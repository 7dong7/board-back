package mystudy.study.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.security.jwt.JWTUtil;
import mystudy.study.security.oauth2.user.CustomOAuth2User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    
    // 로그인 성공시 응답
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("CustomSuccessHandler onAuthenticationSuccess: {}", authentication);

        // OAuth2User
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // JWT 토큰 재료
        String username = customOAuth2User.getEmail();
        String nickname = customOAuth2User.getNickname();
        Long memberId = customOAuth2User.getMemberId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority(); // 권한

        // 토큰 생성
        String access = jwtUtil.createJWT("access", memberId, username, role, nickname, 15 * 60 * 1000L); // 15분
        String refresh = jwtUtil.createJWT("refresh", memberId, username, role, nickname, 24 * 60 * 60 * 1000L); // 24시간

        // 기존 쿠키 삭제 // refresh 재발급시 중복 쿠키가 생기는 문제가 있음
        response.addCookie(deleteCookie("refresh"));
        response.addCookie(deleteCookie("access"));
        /**
         *  프론트에스 location.href 방식으로 요청을 진행했기 때문에
         *  서버에서는 쿠키에 밖에 담아서 응답할 수 없다
         *      따라서, 프론트에서 SDK(앱) 을 이용해서 header 담아서 사용
         *      혹은, 프론트의 특정 경로로 응답하고 특정 경로의 프론트에서 다시 특정 서버의 경로로 요청
         *          -> 쿠키의 값을 response header 에 담아서 localStorage 에 담아서 사용하는 방식으로 구현해야 한다
         */
        // 응답 설정
        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        // JWT 쿠기에 보내기
//        response.addCookie(createCookie("Authentication", token)); // 토큰 쿠키에 담기
        response.sendRedirect("http://localhost:5173/oauth2/setting/handler"); // 쿠키를 보낼 곳
    }

    // == 쿠키 생성 ==
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value); // 쿠키 만들기
        cookie.setMaxAge(24*60*60); // 쿠키 유효 기간 설정 (초 단위)
        //cookie.setSecure(true); // HTTPS 의 보안 상태에서만 쿠키 유효 설정
        cookie.setPath("/");  // 애플리케이션내의 모든 경로에서 쿠키가 유효하게 설정
        cookie.setHttpOnly(true); // HttpOnly 쿠키가 클라이언트 측 스크립트에서 접근할 수 없게 된다 (XSS) 공격 보호 설정

        return cookie;
    }
    // == 기존 쿠키 삭제 ==
    private Cookie deleteCookie(String name) {

        Cookie cookie = new Cookie(name, null); // 값은 null로 설정
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 동일한 경로 지정
        cookie.setHttpOnly(true); // 기존 쿠키와 동일한 속성 유지

        return cookie;
    }
}
