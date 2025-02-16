package mystudy.study.domain.member.entity;

// 사용자의 권한
public enum RoleType {
    
    USER("일반 사용자"), ADMIN("관리자");

    private final String description;

    private RoleType(String description) {
        this.description = description;
    }
}
