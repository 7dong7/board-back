package mystudy.study.domain.member.entity;

// 사용자의 권한
public enum RoleType {
    
    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");

    private final String description;

    private RoleType(String description) {
        this.description = description;
    }
}
