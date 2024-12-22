package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // 사용자가 작성한 댓글 수 가져오기
    public Long getCommentCountByMemberId(Long id) {
        return commentRepository.getCommentCountByMemberId(id);
    }
}
