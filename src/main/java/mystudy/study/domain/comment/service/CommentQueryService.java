package mystudy.study.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.dto.ViewCommentDto;
import mystudy.study.domain.comment.dto.ViewReplyDto;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.comment.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
@Slf4j
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

    // 게시글 id를 사용해서 댓글 조회 (페이징) ============ 삭제 예정 =============
    public Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable) {
        return commentRepository.getCommentByPostId(postId, commentPageable);
    }

    public Page<ViewCommentDto> getViewComment(Long postId, Pageable commentPageable) {

        // 댓글 페이징
        Page<ViewCommentDto> viewCommentPage = commentRepository.getViewComment(postId, commentPageable);
        log.info("getViewComment viewComment: {}", viewCommentPage);

        // 댓글 id 추출
        List<Long> parentIdList = viewCommentPage.stream()
                .map(ViewCommentDto::getCommentId)
                .collect(toList());

        // 대댓글 가져온기
        List<ViewReplyDto> viewReply = commentRepository.getViewReply(parentIdList);
        log.info("getViewComment viewReply: {}", viewReply);

        // 조회 결과 그룹화
        Map<Long, List<ViewReplyDto>> groupReply = viewReply.stream()
                .collect(Collectors.groupingBy(ViewReplyDto::getParentId));

        // 그룹화되 대댓글 댓글에 추가하기
        viewCommentPage.forEach(viewComment -> {
            // 댓글 commentId 필드와 대댓글의 parentId 필드가 일치되는 경우 parentCommentDto 안의 (replies) 대댓글에 넣어주기
            List<ViewReplyDto> replies = groupReply.getOrDefault(viewComment.getCommentId(), new ArrayList<>());
            viewComment.getReplies().addAll(replies);
        });

        return viewCommentPage;
    }
}
