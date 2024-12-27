package mystudy.study.repository;

import mystudy.study.domain.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom{

    // 사용자가 작성한 댓글 수
    Long getCommentCountByMemberId(Long id);

    // 사용자가 작성한 댓글 가져오기 (페이징)
    Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable);

    // 게시글에 달려있는 댓글 가져오기 (페이징)
    Page<CommentDto> getCommentByPostId(Long postId, Pageable commentPageable);
}
