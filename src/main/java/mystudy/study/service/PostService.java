package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.domain.dto.post.PostViewDto;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public PostViewDto getPostView(Long postId) {

        // 조회수 증가
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        post.increaseViewCount();

        // 게시글 가져오기
        return postRepository.getPostView(postId);
    }
}
