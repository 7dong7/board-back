package mystudy.study.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // == 다중 토큰  검증 == //
        
        // header access 토큰 확인
        String headerAccessToken = request.getHeader("access");
        log.info("JWTAuthFilter doFilterInternal headerAccessToken: {}", headerAccessToken);

        if (headerAccessToken == null) { // 토큰이 없는 경우
            filterChain.doFilter(request, response);
            return;
        }

        // 다음 필터로
        filterChain.doFilter(request, response);
    }
}
