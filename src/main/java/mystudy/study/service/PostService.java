package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import mystudy.study.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 게시글 검색해서 가져오기
    @Transactional
    public Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition) {
        return postRepository.getPostPage(pageable, condition);
    }
    
    // 게시글 수 가져오기
    public Long getPostCountByMemberId(Long id) {
        return postRepository.getPostCountByMemberId(id);
    }

}
