package mystudy.study.config;

public class AccessURL {

    public static final String[] RESOURCE = {
        "/css/**", "/js/**", "/favicon.ico",
    };

    public static final String[] WHITELIST = { // 권한이 없어도 접근 가능한
        "/login", "/", "/join"
    };
    
    public static final String[] BLACKLIST = { // 접근 불가
    };

    // 권한이 USER 인 사용자만 가능
    public static final String[] USER_ONLY_ROUTE = { // 유저만 접근가능
    };

    // 권한이 ADMIN 인 사용자만 가능
    public static final String[] ADMIN_ONLY_ROUTE = { //
        "/admin/**"
    };

}
