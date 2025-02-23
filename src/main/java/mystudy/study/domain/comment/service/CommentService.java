package mystudy.study.domain.comment.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.comment.dto.*;
import mystudy.study.domain.comment.entity.Comment;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.comment.repository.CommentRepository;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.post.service.PostQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;
    private final CommentQueryService commentQueryService;

    // 댓글 작성(comment)
    public void writeComment(Long postId, WriteCommentForm writeCommentForm) {
        /**
         *  1. 로그인 여부 확인 -> AccessURL 설정
         *  2. 게시글 존재 여부 확인 -> 존재하지 않는 게시글에 임의적으로 댓글을 작성하려고 하는 경우 (정상적이지 않은 경우 - postId 조작)
         *  3. 댓글 작성
         */
        // 게시글 조회
        Post post = postQueryService.findByPostId(postId); // 정상적이지 않은 경우 예외 발생
        
        // 로그인 회원 DB 에서 조회
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberQueryService.findByEmail(email);
        
    // 댓글 작성 처리
        // 댓글 생성
        Comment comment = Comment.builder()
                .content(writeCommentForm.getContent())
                .build();

        // 댓글 연관관계 매핑
        comment.addPost(post); // 양방향 설정 -> DB와 entity 객체의 동기화
        comment.addMember(member); // 양방향 설정
        
        // 댓글 저장
        commentRepository.save(comment);
    }





    // 댓글 id를 parentId 로 사용하는 댓글 조회 (대댓글 조회, where 절에서 in 사용해서 한번에 조회)
    public List<ReplyCommentDto> getCommentByParentId(List<Long> parentIdList) {
        return commentRepository.getCommentByParentId(parentIdList);
    }



    // 게시물에 댓글 작성  ============= 삭제 예정 =================
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
