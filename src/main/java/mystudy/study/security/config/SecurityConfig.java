package mystudy.study.security.config;

import lombok.RequiredArgsConstructor;
import mystudy.study.security.jwt.JWTAuthFilter;
import mystudy.study.security.jwt.LoginFilter;
import mystudy.study.security.oauth2.service.CustomOAuth2UserService;
import mystudy.study.security.jwt.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;

    // 인증 매니저
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 역할 계층
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role("ADMIN").implies("USER") // ADMIN > USER
                .build();
    }

    // 리소스 접근 설정 (모든 필터 체인에 적용)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스에 대한 시큐리티 필터 접근제한 X
        return web -> web.ignoring().requestMatchers(AccessURL.RESOURCE);
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // cors 설정 생성
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // 허용 출처
        config.setAllowedMethods(Arrays.asList("GET","POST")); // 허용 메소드
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 요청 허용 (받는거)

        // 내가 작성한 Header 값이 노출되어야 프론트에서 사용할 수 있다
        config.addExposedHeader("access"); // access header 값 노출 허용
        config.setAllowCredentials(true); // 쿠키/인증 정보 허용
        
        // 적용 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 요청
        return source;
    }
    
    // 폼로그인 + JWT 필터 체인
    @Bean
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화 & basic 로그인 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        // cors 설정
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                );
        // 인가 설정
        http
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(AccessURL.ADMIN_ROUTE).hasAnyRole("ADMIN")
                        .requestMatchers(AccessURL.USER_ROUTE).hasAnyRole("USER")
                        .requestMatchers(AccessURL.WHITELIST).permitAll()
                        .requestMatchers(AccessURL.BLACKLIST).denyAll()
                        .anyRequest().authenticated()
                );


        // 폼 로그인 방식 사용
        http
                .formLogin(form -> form
                        .loginPage("/login")
//                        .loginProcessingUrl("/login") // 로그인 처리 URL
                );

        // ==== form login JWT 방식 ==== //
        // 세션 statelsee 상태 설정
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        // 폼 로그인 방식으로 로그인을 진행하고 JWT 발급을 위해서는 필터의 successHandler 가 필요하기 때문에
        // 필터를 커스텀해서 만들어야 한다 // UsernamePasswordAuthenticationFilter 를 LoginFilter 로 대체
        http
                .addFilterAt(
                        new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class
                );
        // 토큰 검증 필터 추가 (LoginFilter 전에 추가)
        http
                .addFilterAfter(
                        new JWTAuthFilter(jwtUtil), LoginFilter.class
                );




        // oauth2 로그인 방식 사용
        http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 로그인 페이지
                        .userInfoEndpoint( userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        // 외부 인증 제공자 엔드포인트 지정
                        // 회원의 프로필 정보를 조회할 때 사용
                );
        
        // 로그아웃
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")           // 로그아웃 경로
                        .logoutSuccessUrl("/login?logout")     // 로그아웃 이후 경로
                        .invalidateHttpSession(true)    // 세션 무효화
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
