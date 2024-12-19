package mystudy.study.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.PostSearchCondition;
import mystudy.study.domain.entity.Post;
import mystudy.study.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void getPostPage(Pageable pageable, PostSearchCondition condition) {
        postRepository.getPostPage(pageable, condition);
    }

}
