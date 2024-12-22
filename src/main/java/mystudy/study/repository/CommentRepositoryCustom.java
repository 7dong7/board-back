package mystudy.study.repository;

import mystudy.study.domain.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom{

    // 사용자가 작성한 댓글 수
    Long getCommentCountByMemberId(Long id);

    // 사용자가 작성한 댓글 페이징 가져오기
    Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable);
}
