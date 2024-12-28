package mystudy.study.repository;

import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.CommentViewDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom{

    // 사용자가 작성한 댓글 수
    Long getCommentCountByMemberId(Long id);

    // 사용자가 작성한 댓글 가져오기 (페이징)
    Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable);

    // 게시글에 달려있는 댓글 가져오기 (페이징)
    Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable);

    // 조회된 댓글의 대댓글 조회 (where 절에서 in 사용해서 한번에 조회)
    List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList);
}
