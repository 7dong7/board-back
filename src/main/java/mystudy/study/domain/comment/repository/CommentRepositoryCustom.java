package mystudy.study.domain.comment.repository;

import mystudy.study.domain.comment.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom{

    // 회원 id를 사용해서 작성한 전체 댓글 수 조회
    Long getCommentCountByMemberId(Long id);

    // 회원 정보 조회 - 회원가 작성한 댓글 조회 (페이징)
    Page<CommentDto> getCommentByMemberId(Long memberId, Pageable pageable);

    // 게시글 조회 - 댓글 조회 (페이징) : 댓글 페이징 조회후  대댓글 배치 조회
    Page<ViewCommentDto> getViewComment(Long postId, Pageable commentPageable);

    // 게시글 조회 - 대댓글 조회 : 댓글 부모Id 사용 배치 조회
    List<ViewReplyDto> getViewReply(List<Long> parentIdList);

}
