package mystudy.study.domain.member.dto.search;

public enum MemberSearchType {
    NAME("이름"),
    EMAIL("이메일"),
    NICKNAME("닉네임");

    private final String typeName;

    private MemberSearchType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

}
