package mystudy.study.domain.member.entity;

// 회원의 권한
public enum RoleType {
    
    ROLE_USER("회원"),
    ROLE_ADMIN("관리자");

    private final String description;

    private RoleType(String description) {
        this.description = description;
    }
}
