package mystudy.study.config;

import lombok.RequiredArgsConstructor;
import mystudy.study.jwt.JWTAuthFilter;
import mystudy.study.jwt.JWTUtil;
import mystudy.study.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static mystudy.study.config.AccessURL.*;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 리소스 접근 설정 (모든 필터 체인에 적용)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(RESOURCE);
    }

    // 폼로그인 + JWT 필터 체인
    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화 & basic 로그인 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 세션 statelsee
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 인가 설정
        http
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(WHITELIST).permitAll()
                        .anyRequest().authenticated()
                );

        // 폼 로그인 방식 사용
        http
                .formLogin(form -> form
                        .loginPage("/login")
                );

        // 폼 로그인 방식으로 로그인을 진행하고 JWT 발급을 위해서는 필터의 successHandler 가 필요하기 때문에
        // 필터를 커스텀해서 만들어야 한다 // UsernamePasswordAuthenticationFilter 를 LoginFilter 로 대체
        http
                .addFilterAt(
                        new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class
                );
        // 토큰 검증 필터 추가 (LoginFilter 전에 추가)
        http
                .addFilterBefore(
                        new JWTAuthFilter(jwtUtil), LoginFilter.class
                );

        return http.build();
    }



}
