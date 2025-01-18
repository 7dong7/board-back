package mystudy.study.repository.comment;

import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom{

    // 사용자 id를 사용해서 작성한 전체 댓글 수 조회
    Long getCommentCountByMemberId(Long id);

    // 사용자 id를 사용해서 댓글 조회 (페이징)
    Page<CommentDto> getCommentByMemberId(Long memberId, Pageable pageable);

    // 게시글 id를 사용해서 댓글 조회 (페이징)
    Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable);

    // 댓글 id를 parentId 로 사용하는 댓글 조회 (대댓글 조회, where 절에서 in 사용해서 한번에 조회)
    List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList);
}
