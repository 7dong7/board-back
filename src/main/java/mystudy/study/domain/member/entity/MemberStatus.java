package mystudy.study.domain.member.entity;

public enum MemberStatus {
    ACTIVE("활성"),
    DELETE("삭제");

    private String description;

    private MemberStatus(String description) {
        this.description = description;
    }
}
