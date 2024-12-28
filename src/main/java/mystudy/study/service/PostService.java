package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.comment.CommentDto;
import mystudy.study.domain.dto.comment.CommentViewDto;
import mystudy.study.domain.dto.comment.ParentCommentDto;
import mystudy.study.domain.dto.comment.ReplyCommentDto;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.PostViewDto;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.CommentRepository;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    /**
     * @Transactional(readOnly = true)
     * 클래스 레벨에서 @Transactional(readOnly = true)를 선언하고, 변경작업을 수행하는 별도의 @Transactional 을 붙이는 방식이 좋다
     * 
     *
     *  */


    private final PostRepository postRepository;
    private final CommentService commentService;

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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

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
}
