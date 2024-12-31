package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.*;
import mystudy.study.domain.entity.Comment;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
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

    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;

    // 사용자가 작성한 댓글 수 조회
    public Long getCommentCountByMemberId(Long id) {
        return commentRepository.getCommentCountByMemberId(id);
    }

    // 사용자 id를 사용해서 댓글 조회 (페이징)
    public Page<CommentDto> getCommentByMemberId(Long id, Pageable pageable) {
        return commentRepository.getCommentByMemberId(id, pageable);
    };

    // 게시글 id를 사용해서 댓글 조회 (페이징)
    public Page<ParentCommentDto> getCommentByPostId(Long postId, Pageable commentPageable) {
        return commentRepository.getCommentByPostId(postId, commentPageable);
    }

    // 댓글 id를 parentId 로 사용하는 댓글 조회 (대댓글 조회, where 절에서 in 사용해서 한번에 조회)
    public List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList) {
        return commentRepository.getCommentByParentId(parentIdList);
    }

    // postId 댓글 작성
    @Transactional
    public void newComment(NewCommentDto newCommentDto) {

        // 글 작성자 ( 로그인 정보로 가져와야 함 )
        Member member = memberQueryService.findMemberById(3L);

        // 게시글 조회
        Post post = postQueryService.findById(newCommentDto.getPostId());

        // 새로운 댓글(comment) 생성
        Comment comment = Comment.builder()
                .content(newCommentDto.getContent())
                .member(member)
                .post(post)
                .build();

        // 댓글 저장
        commentRepository.save(comment);

    }
}
