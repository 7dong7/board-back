package mystudy.study.domain.comment.entity;

public enum CommentStatus {

    ACTIVE("활성"),
    DELETE("삭제");

    private final String description;

    private CommentStatus(String description) {
        this.description = description;
    }

}
