package mystudy.study.domain.post.entity;

public enum PostStatus {
    ACTIVE("활성"),
    DELETE("삭제");

    private final String description;

    private PostStatus(String description) {
        this.description = description;
    }
}
