package mystudy.study.repository;

public interface CommentRepositoryCustom{

    // 사용자가 작성한 댓글 수
    Long getCommentCountByMemberId(Long id);
}
