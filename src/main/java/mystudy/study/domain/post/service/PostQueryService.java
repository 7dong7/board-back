package mystudy.study.domain.post.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.post.entity.Post;
import mystudy.study.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {

    private final PostRepository postRepository;

    // id 게시글 조회
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow( () -> new IllegalArgumentException("해당하는 게시글이 존재하지 않습니다."));
    }

    /**
     * @param postId
     *  - Post 객체를 찾기 위한 id 값
     * @return Post
     *  - 삭제되지 않은 Post 조회 (PostStatus.ACTIVE)
     *  - Optional 처리 후 Post 객체 반환
     * @throws
     *  - 정상적인 요청이 아니라 임의적으로 클라이언트를 조작해서 접근하는 경우 해당하는 postId 가 존재하지 않을 수 있다.
     *  - 그러한 경우 IllegalArgumentException 예외가 발생한다
     */
    // postId 로 게시글 조회 (fetch join) queryDSL
    public Post findByPostId(Long postId) {
        return postRepository.findByPostId(postId)
                .orElseThrow( () -> new IllegalArgumentException("잘못된 게시글 요청입니다. postId = " + postId));
    }
}
