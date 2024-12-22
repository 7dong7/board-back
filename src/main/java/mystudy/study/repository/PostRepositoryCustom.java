package mystudy.study.repository;

import mystudy.study.domain.dto.post.PostDto;
import mystudy.study.domain.dto.post.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition);

    Long getPostCountByMemberId(Long id);

    Page<PostDto> getPostByMemberId(Long id, Pageable pageable);
}
