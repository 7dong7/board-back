package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.CommentViewDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import mystudy.study.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 사용자가 작성한 댓글 수 가져오기
    public Long getCommentCountByMemberId(Long id) {
        return commentRepository.getCommentCountByMemberId(id);
    }

    // 사용자가 작성한 댓글 가져오기 (페이징)
    public Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable) {
        return commentRepository.getCommentByMemberId(id, pageable);
    };

    // 게시글에 작성한 댓글 가져오기 (페이징)
    public Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable) {
        return commentRepository.getCommentByPostId(postId, commentPageable);
    }

    // 조회된 댓글의 대댓글 조회 (where 절에서 in 사용해서 한번에 조회)
    public List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList) {
        return commentRepository.getCommentByParentId(parentIdList);
    }
}
