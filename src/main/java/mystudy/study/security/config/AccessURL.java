package mystudy.study.security.config;

public class AccessURL {

    public static final String[] RESOURCE = {
            "/css/**", "/js/**", "/favicon.ico",
            "/images/**", "/upload/**"
    };

    public static final String[] WHITELIST = { // 권한이 없어도 접근 가능한
            "/",        // 메인 페이지
            "/login", "/logout",        // 로그인 & 로그아웃
            "/members", "/members/*",   // 회원
            "/posts", "/posts/*",       // 게시글
            "/oauth2/**",               // oauth2 로그인

            // 사용자 변경 예정 role : ROLE_USER
            "/members/*/edit",
            "/image/upload",
            "/posts/new/post",
            "/posts/*/edit"

            // 사용자 변경 예정 role : ROLE_ADMIN
    };

    public static final String[] BLACKLIST = { // 접근 불가
    };

    // 권한이 USER 인 사용자만 가능
    public static final String[] USER_ROUTE = { // 유저 접근가능
    };

    // 권한이 ADMIN 인 사용자만 가능
    public static final String[] ADMIN_ROUTE = { //
            "/admin", "/admin/**",
    };

}
