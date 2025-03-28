package mystudy.study.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.entity.MemberStatus;
import mystudy.study.domain.member.entity.RoleType;
import mystudy.study.security.CustomUserDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {
// JWT 검증 필터

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // == 다중 토큰 검증 == //
//        StringBuffer requestURL = request.getRequestURL(); // "http://localhost:8080/login"
        // 토큰 검증을 하지않는 경로 설정
        String requestURI = request.getRequestURI(); // "/login"
        log.info("JWTAuthFilter requestURI: {}", requestURI);
        if (requestURI.equals("/login")) {
            log.info("/login 요청시 토큰 검증 뛰어넘기");
            filterChain.doFilter(request, response);
            return;
        }

    // ===== 토큰 유효성 검증 ===== //
        // access Token 받기
        String authorization = request.getHeader("Authorization");
        log.info("토큰 유효성 검사 실행 JWTAuthFilter doFilterInternal authorization: {}", authorization);
        
        // access token 확인
        if (authorization == null || !authorization.startsWith("Bearer ")) { // 내가 원하는게 아닌 경우
            /**
             *  토큰의 경우 있는 경우와 없는 경우
             *      - 토큰이 없는 경우
             *      로그인을 하지 않은 경우  이후의 필터 "AuthorizationFilter" 에서 
             *      security filter chain 에 있는 authorizeHttpRequests 에 등록된 경로와 권한을 확인한다
             *          그냥 permitAll() 인 경우는 통과
             *          그렇지 않으면 "에러" 발생
             *      
             *      - 토큰이 있는 경우
             *          아래의 유효성 검증을 진행
             */
            log.info("인증 필터 access token 이 없습니다"); 
            // 응답 상태 코드
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            filterChain.doFilter(request, response); // 다음 필터에 넘겨주기
            return;
        }

        // Bearer 토큰값
        String accessToken = authorization.split(" ")[1];

        // == 토큰 검증 ==
        try {
            // 정상적이지 않은 토큰의 경우 여기서 exception 을 발생함
            response.setContentType("application/json; charset=utf-8");
            Claims claims = jwtUtil.validClaims(accessToken);
            log.info("claims 검증 통과");
            
        // ====== 토큰 검증 통과 ===== 세션생성 (일시적)
            // 토큰에서 username, role 값을 획득
            String username = jwtUtil.getUsername(accessToken); // username & email
            String nickname = jwtUtil.getNickname(accessToken);
            String role = jwtUtil.getRole(accessToken);

            // authentication 객체 생성
            Member member = Member.builder()
                    .email(username)
                    .role(RoleType.valueOf(role))
                    .nickname(nickname)
                    .build();
            /**
             *  access 토큰을 통해 인증이 완료되면 context 에 authentication 객체를 저장해 일시적으로 세션을 활성화(연결)시킨다
             *  oauth2 방식으로 구현시 인증객체로 OAuth2User 를 커스텀해서 만든것도 사용해야 됨
             */
            CustomUserDetail authMember = new CustomUserDetail(member);

            Authentication authentication = new UsernamePasswordAuthenticationToken(authMember, null, authMember.getAuthorities());

            boolean authenticated = authentication.isAuthenticated();
            log.info("인증된 객체 생성 [isAuthenticated(): {}, role: {}]", authenticated, authMember.getAuthorities());

            // 인증 객체 저장
            // SecurityContextHolder 에 인증객체를 저장하면 일시적으로 세션생성, 로그인된 상태로 변경된다
            SecurityContextHolder.getContext().setAuthentication(authentication);
            /**
             *  response status code
             *  응답에 관련된 상태코드를 프론트 쪽으로 응답해준다
             *  프론트 쪽에서는 응답받은 상태 코드에 따라서 어떤 기능을 수행할지 결정한다 (미리 상태코드에 대한 약속을 해 두어야 한다)
             *  access 토큰이 만료되었으니까 refresh 토큰을 사용해서 access 토큰을 재발급 받는 경로, 기능을 수행하게 한다
             *  토큰 만료 -> refresh 토큰 요청 -> access 토큰 재발급 -> 다시 요청
             *  상태 코드에 따라서 결정
             */
            // 토큰의 검증이 모두 완료된 경우
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) { // 토큰 존재 유무 - null => IllegalArgumentException
            log.info("토큰 값이 없음");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"token is null\", \"message\": \"토큰이 없습니다.\"}");
            return;
        } catch (MalformedJwtException e) { // 토큰 형식 ( header.payloaad.signature 아님 ) - MalformedJwtException
            log.info("토큰의 형식이 정상이 아님");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"The format is incorrect\", \"message\": \"토큰 형식이 유효하지 않습니다.\"}");
            return;
        } catch (SignatureException e) { // 서명 유효(secretKey 일치 여부) - SignatureException
            log.info("토큰의 서명이 유효하지 않음");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"The signature is invalid\", \"message\": \"유효하지 않은 서명입니다.\"}");
            return;
        } catch (ExpiredJwtException e) { // 토큰 만료 여부: expired => ExpiredJwtException
            log.info("토큰이 만료됨");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"access token expired\", \"message\": \"액세스 토큰이 만료되었습니다.\"}");
            return;
        }
    }
}
