package mystudy.study.domain.comment.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.CommentDto;
import mystudy.study.domain.comment.dto.NewCommentDto;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.dto.ReplyCommentDto;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.comment.repository.CommentRepository;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.post.service.PostQueryService;
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
    private final CommentQueryService commentQueryService;

    // 회원가 작성한 댓글 수 조회
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

    // 댓글 id를 parentId 로 사용하는 댓글 조회 (대댓글 조회, where 절에서 in 사용해서 한번에 조회)
    public List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList) {
        return commentRepository.getCommentByParentId(parentIdList);
    }

    // 게시물에 댓글 작성
    @Transactional
    public void newComment(NewCommentDto newCommentDto, Long loginMemberId) {

        // 글 작성자   로그인 회원
        Member member = memberQueryService.findMemberById(loginMemberId);

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

    // 게시물 대댓글 작성하기
    @Transactional
    public void newReply(NewCommentDto newCommentDto, Long loginMemberId) {

        // 글 작성자 ( 로그인 정보로 가져와야 함 )
        Member member = memberQueryService.findMemberById(loginMemberId);

        // 게시글 조회
        Post post = postQueryService.findById(newCommentDto.getPostId());

        // 부모 댓글 조회
        Comment parentComment = commentQueryService.findCommentById(newCommentDto.getParentId());

        // 새로운 댓글(comment) 생성
        Comment newComment = Comment.builder()
                .content(newCommentDto.getContent())
                .member(member)
                .post(post)
                .parent(parentComment)
                .build();

        // 댓글 저장
        commentRepository.save(newComment);
    }
}
