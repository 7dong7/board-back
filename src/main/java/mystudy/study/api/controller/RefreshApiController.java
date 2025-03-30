package mystudy.study.api.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.security.CustomUserDetail;
import mystudy.study.security.CustomUserDetailsService;
import mystudy.study.security.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


@Slf4j
@RestController
@RequiredArgsConstructor
public class RefreshApiController {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;


    @PostMapping("/api/refresh")
    public ResponseEntity<String> refreshApi(HttpServletRequest request, HttpServletResponse response) {
        log.info("Access 토큰 재발급 실행");

        // 쿠키에서 refresh 토큰값 추출
        String requestRefresh = Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        log.info("refresh 토큰 값: {}", requestRefresh);
        
        // refresh 토큰 값이 없는 경우 비정상적인 요청
        if(requestRefresh == null) { // refresh 토큰이 없는 경우 & 비정상적인 요청
            return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
        }

        // 토큰 검증 및 발급
        try {
            // refresh 토큰 파싱 & 비정상시 Exception 발생
            Claims claims = jwtUtil.validClaims(requestRefresh);
            // 파싱이 성공하면 유효한 토큰
            String email = jwtUtil.getUsername(requestRefresh);
            Long memberId = jwtUtil.getMemberId(requestRefresh);

            CustomUserDetail userDetails = (CustomUserDetail) customUserDetailsService.loadUserByUsername(email);
            String username = userDetails.getUsername(); // email
            String nickname = userDetails.getNickname();

            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority(); // 권한

            log.info("jwt 생성 전 정보 [username: {}, nickname: {}, role: {}]", username, nickname, role);

            // 토큰 생성
            String access = jwtUtil.createJWT("access", memberId, username, role, nickname, 15 * 60 * 1000L); // 15분
            String refresh = jwtUtil.createJWT("refresh", memberId, username, role, nickname, 24 * 60 * 60 * 1000L); // 24시간

            // 기존 쿠키 삭제
            response.addCookie(deleteCookie("refresh"));

            // 응답 설정
            response.setHeader("access", access); // access token 헤더에 추가
            response.addCookie(createCookie("refresh", refresh));
            response.setStatus(HttpStatus.OK.value());

            return new ResponseEntity<>("ok", HttpStatus.OK);
        } catch (Exception e) {
            // 만료 & 서명 & null & 포맷 중 하나가 정상이 아닌 경우 => 다시 로그인을 시키도록 한다
            return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
        }
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
        cookie.setHttpOnly(true); // 기존 쿠키와 동일한 속성 유지
        cookie.setPath("/"); // 동일한 경로 지정
        cookie.setMaxAge(0); // 즉시 만료
        return cookie;
    }
}
