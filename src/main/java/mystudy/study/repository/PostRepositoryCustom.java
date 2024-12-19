package mystudy.study.repository;

import mystudy.study.domain.dto.PostDto;
import mystudy.study.domain.dto.PostSearchCondition;
import mystudy.study.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostDto> getPostPage(Pageable pageable, PostSearchCondition condition);
}
