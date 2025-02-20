package mystudy.study.security.config;

public class AccessURL {

    public static final String[] RESOURCE = {
            "/css/**", "/js/**", "/favicon.ico",
            "/images/**", "/upload/**"
    };

    public static final String[] WHITELIST = { // 권한이 없어도 접근 가능한
            "/",
            "/login",
            "/logout",
            "/posts", "/posts/*",
            "/members", "/members/*",
            "/oauth2/**",

            "/image/upload", // 사용자 변경 예정 role : ROLE_USER

            "/posts/new/post", // 사용자 변경 예정 role : ROLE_USER
            "/posts/*/edit" // 사용자 변경 예정 role : ROLE_USER
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
