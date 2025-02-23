package mystudy.study.domain.comment.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.comment.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;

    // parentId 를 commentId로 가지는 댓글 조회
    public Comment findCommentById(Long parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));

    }

    // 회원이 작성한 댓글 수 조회
    public Long getCommentCountByMemberId(Long id) {
        return commentRepository.getCommentCountByMemberId(id);
    }

    // 회원 정보 조회 - 회원가 작성한 댓글 조회 (페이징)
    public Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable) {
        return commentRepository.getCommentByMemberId(id, pageable);
    };

    // 게시글 id를 사용해서 댓글 조회 (페이징)
    public Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable) {
        return commentRepository.getCommentByPostId(postId, commentPageable);
    }
}
