package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.entity.Comment;
import mystudy.study.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentRepository commentRepository;

    // parentId 를 commentId로 가지는 댓글 조회
    @Transactional
    public Comment findCommentById(Long parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));

    }
}
