package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import mystudy.study.domain.dto.post.NewPostDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.PostViewDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentService commentService;

    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;

    // 게시글 검색해서 가져오기
    public Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition) {
        return postRepository.getPostPage(pageable, condition);
    }
    
    // 게시글 수 가져오기
    public Long getPostCountByMemberId(Long id) {
        return postRepository.getPostCountByMemberId(id);
    }
    
    // 사용자가 작성한 게시글 가져오기, 사용자 정보에서 사용
    public Page<PostDto> getPostByMemberId(Long id, Pageable pageable) {
        return postRepository.getPostByMemberId(id, pageable);
    }

    // 게시글 조회
    @Transactional // 조회수 증가
    public PostViewDto getPostView(Long postId, Pageable commentPageable) {

        // 조회수 증가
        Post post = postQueryService.findById(postId);
        post.increaseViewCount();

        // 게시글 조회
        PostViewDto postView = postRepository.getPostView(postId);

        // 댓글 조회 ( 페이징 )
        Page<ParentCommentDto> parentCommentDtoPage = commentService.getCommentByPostId(postId, commentPageable);

            // 조횐된 댓글 id 리스트
        List<Long> parentIdList = parentCommentDtoPage.stream()
                .map(ParentCommentDto::getCommentId)
                .toList();

            // 조회된 댓글의 대댓글 조회 (where 절에서 in 사용해서 한번에 조회)
        List<ReplyCommentDto> replies = commentService.getCommentByParentId(parentIdList);


            // 조회한 대댓글 parentId 변로 구분
        Map<Long, List<ReplyCommentDto>> groupReplyMap = replies.stream()
                .collect(Collectors.groupingBy(ReplyCommentDto::getParentId));

            // parentId에 해당하는 값이 없으면 빈 리스트 추가
        parentCommentDtoPage.forEach(parentCommentDto -> {
                // 댓글 commentId 필드와 대댓글의 parentId 필드가 일치되는 경우 parentCommentDto 안의 (replies) 대댓글에 넣어주기
            List<ReplyCommentDto> reply = groupReplyMap.getOrDefault(parentCommentDto.getCommentId(), new ArrayList<>());
            parentCommentDto.getReplies().addAll(reply);
        });

        // postView 안에 List<ParentCommentDto> 에 추가
        postView.addComments(parentCommentDtoPage); 

        return postView;
    }

    // 새로은 게시글 작성
    @Transactional
    public void createPost(NewPostDto newPostDto) {

        // 사용자 정보 ( 로그인 정보로 가져오도 )
        Member member = memberQueryService.findMemberById(3L);

        Post post = Post.builder()
                .title(newPostDto.getTitle())
                .content(newPostDto.getContent())
                .member(member)
                .build();

        member.getPosts().add(post);

/*
* public class NewPostDto {
    private String title; // 게시글 제목
    private String content; // 게시글 내용
* */

    /*
        @Column(name = "post_id", updatable = false)
        private Long id;
        private String title;

        @Lob
        private String content;
        private Integer viewCount = 0;

        @ToString.Exclude
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", updatable = false)
        private Member member;

        @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Comment> comments = new ArrayList<>();
    * */

    }
}
