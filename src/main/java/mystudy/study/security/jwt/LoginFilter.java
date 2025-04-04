package mystudy.study.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.exception.AccountDeletedException;
import mystudy.study.security.CustomUserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// 로그인 필터  // 로그인은 form 로그인 방식으로 JWT 를 발급하기 위해서
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // 로그인 정보를 서버에서 받는다
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그인 입력 정보 시작 ");

    // == 클라이언트 사이드 렌더링 & 프론트에서 JSON 방식으로 값을 서버로 보내는 경우 ==
        try {
        // JSON 데이터 파싱
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> form = mapper.readValue(request.getInputStream(), Map.class);
            String username = form.get("username");
            String password = form.get("password");
            log.info("LoginFilter attemptAuthentication 사용자 정보 아이디: {},비밀번호 {}", username, password);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("JSON 값 파싱 실패", e);
        }

    // == 서버 사이드 랜더링 방식 & form Login 방식의 경우 ==
        // 요청에서 username, password 추출
//        String username = obtainUsername(request);
//        String password = obtainPassword(request);
//
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
//
//        return authenticationManager.authenticate(token);
    }

    
    
    
    // 로그인 성공시 응답
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // == 다중 토큰 == //
        log.info("loginFilter successfulAuthentication 로그인 성공 처리");

        // 회원 정보
        String username = authentication.getName();
        String nickname = null;
        Long memberId = null;
        if (authentication.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            nickname = customUserDetail.getNickname();
            memberId = customUserDetail.getMemberId();
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority(); // 권한

        // 토큰 생성
        String access = jwtUtil.createJWT("access", memberId, username, role, nickname, 15 * 60 * 1000L); // 15분
        String refresh = jwtUtil.createJWT("refresh", memberId, username, role, nickname, 24 * 60 * 60 * 1000L); // 24시간
        // String category, Long memberId, String username, String role, String nickname, Long expiredMs
        
        // 기존 쿠키 삭제 // refresh 재발급시 중복 쿠키가 생기는 문제가 있음
        response.addCookie(deleteCookie("refresh"));

        // 응답 설정
        response.setHeader("access", access); // access token 헤더에 추가
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    // 로그인 실패시 응답
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        /**
         *  로그인을 실패했다
         *      1. 탈퇴한 회원
         *          member5@naver.com
         *          test!
         *          
         *      2. 비밀번호를 틀린 회원
         *          member1@naver.com
         *          아무값
         *      
         *      3. 존재하지 않는 회원
         *          아무값
         *          아무값
         */
        log.info(" 로그인 실패시에 나타내는 응답입니다. ");
        log.info("\nmessage: {}, \nsimpleName: {}", failed.getMessage(), failed.getClass().getSimpleName());

        // 어떤 경우의 로그인 실패인가?
            // 응답 설정 기본 설정
        response.setContentType("application/json;charset=UTF-8");
        String message; // 응답 메시지 설정
        // 1. 탈퇴한 회원의 경우
        if (failed.getCause() instanceof AccountDeletedException) {
            log.info("탈퇴한 계정의 경우 AccountDeletedException ");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 응답 상태
            message = "탈퇴한 계정은 사용할 수 없습니다."; // 응답 메시지
        } else if (failed instanceof BadCredentialsException) {
        // 2. 비밀번호를 틀린 회원의 경우
            // BadCredentialsException 의 경우 자격증명중 비밀번호(password) 가 올바르지 않은 경우
            log.info("비밀번호를 틀린 경우 BadCredentialsException");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답 상태
            message = "아이디 또는 비밀번호가 잘못되었습니다.";  // 응답 메시지
        } else {
        // 3. 그냥 잘못된 경우
            log.info("아이디를 틀린 경우");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답 상태
            message = "아이디 또는 비밀번호가 잘못되었습니다.";  // 응답 메시지
        }

        // 응답 형태 설정
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);

        // 응답
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
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
