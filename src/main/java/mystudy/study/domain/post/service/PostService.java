package mystudy.study.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.comment.dto.ParentCommentDto;
import mystudy.study.domain.comment.dto.ReplyCommentDto;
import mystudy.study.domain.comment.service.CommentQueryService;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.post.dto.*;
import mystudy.study.domain.post.repository.PostRepository;
import mystudy.study.domain.comment.service.CommentService;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.security.CustomUserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentService commentService;

    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;
    private final CommentQueryService commentQueryService;

    // 게시글 작성 : 처리
    public void createPost(NewPostDto newPostDto) {

        // 로그인한 회원 확인
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // username 로그인 사용값
        Member member = memberQueryService.findByEmail(email);

        // 게시글 생성
        Post post = Post.builder()
                .title(newPostDto.getTitle())
                .content(newPostDto.getContent())
                .member(member)
                .build();

        // 게시글 연관관계 매핑 cascade 옵션이 있다면 아래의 save 를 사용하지 않아도 DB에 저장된다 (더티체킹)
        member.addPost(post);

        // 게시글 저장
        postRepository.save(post);
    }

    // 게시글 조회 (postId 사용)
    public ViewPostDto getViewPost(Long postId) {
    /**
     *  postId를 사용해서 게시글을 조회한다
     *  게시글의 조회수를 +1 한다
     *  게시글을 ViewPostDto로 변환해서 반환한다
     */
        // 게시글 조회
        Post post = postQueryService.findById(postId); // optional 처리됨

        // 조회수 증가
        post.increaseViewCount();

        /**
         *  지연로딩 방식으로 인해 post안에 member에 접근할 때마다 쿼리문이 발생하므로 
         *  DTO의 경우 집적 따라 조회하도록 한다
         *  예)
         *  ViewPostDto viewPostDto = new ViewPostDto();
         *  viewPostDto.setMemberId(post.getMember().getId()); // 쿼리문 발생
         *  viewPostDto.setNickname(post.getMember().getNickname());
         */
        // ViewPostDto 직접 조회
        return postRepository.getViewPostDto(postId);
    }

    // 게시글 수정 : 페이지 - 게시글 조회 (postId 사용)
    public PostEditDto viewPostEdit(Long postId) {
        /**
         * 게시글을 수정하기 위해 수정 페이지를 요천한다
         * 페이지 요청시 게시글의 작성자와 현재 로그인한 회원의 id를 비교해 적절한 요청인지 확인한다
         */
        // 게시글을 조회 (PostEditDto) 직접 조회
        return postRepository.getPostEditDtoByPostId(postId);
    }

    // 게시글 수정 : 처리 - 게시글 수정하기
    public void postEdit(PostEditDto postEditDto) throws AccessDeniedException {
        // 게시글 조회
        Post post = postQueryService.findByPostId(postEditDto.getPostId());

        // 로그인 Member 확인
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long memberId = null;
            // principal 의 객체가 UserDetails 인지 OAuth2User 인지 확인
        if (principal instanceof CustomUserDetail customUser) {
            memberId = customUser.getMemberId();
        }

        // 게시글의 주인과 로그인 회원 비교
        if (post.getMember().getId().equals(memberId)) {// 같은 경우 (정상)
            post.updateContent(postEditDto.getContent());
        } else { // 작성자가 일치하지 않음
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

    }

    // 게시글 삭제
    public void deletePost(Long postId) throws AccessDeniedException {
        // 게시글 조회
        Post post = postQueryService.findByPostId(postId);

        // 로그인 Member 확인
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long memberId = null;
        // principal 의 객체가 UserDetails 인지 OAuth2User 인지 확인
        if (principal instanceof CustomUserDetail customUser) {
            memberId = customUser.getMemberId();
        }

        // 게시글 주인 로그인 회원 비교
        if (post.getMember().getId().equals(memberId)) {// 같은 경우 (정상)
            // 게시글 삭제 (소프트 삭제)
            post.deletePost();
        } else { // 작성자가 일치하지 않음
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    }

    // 회원 정보 조회 - 회원가 작성한 게시글 조회 (페이징)
    public Page<PostDto> getPostByMemberId(Long id, Pageable pageable) {
        return postRepository.getPostByMemberId(id, pageable);
    }




    // 게시글 조회 // ======================= 삭제 예정 =========================
    public PostViewDto getPostView(Long postId, Pageable commentPageable) { // 조회수 증가

        // 조회수 증가
        Post post = postQueryService.findById(postId);
        post.increaseViewCount();

        // 게시글 조회
        PostViewDto postView = postRepository.getPostView(postId);

        // 댓글 조회 ( 페이징 )
        Page<ParentCommentDto> parentCommentDtoPage = commentQueryService.getCommentByPostId(postId, commentPageable);

        // 조횐된 댓글 id 리스트
        List<Long> parentIdList = parentCommentDtoPage.stream()
                .map(ParentCommentDto::getCommentId)
                .toList();

        // 조회된 댓글의 대댓글 조회 (where 절에서 in 사용해서 한번에 조회)
        List<ReplyCommentDto> replies = commentService.getCommentByParentId(parentIdList);


        // 조회한 대댓글 parentId 별로 구분
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














    // 게시글 검색해서 가져오기
    public Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition) {
        return postRepository.getPostPage(pageable, condition);
    }

    // 게시글 수 가져오기
    public Long getPostCountByMemberId(Long id) {
        return postRepository.getPostCountByMemberId(id);
    }





}
