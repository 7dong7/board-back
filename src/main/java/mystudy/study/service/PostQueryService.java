package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.PostRepository;
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

}
