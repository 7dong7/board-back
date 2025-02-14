package mystudy.study.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostSearchCondition {

    private String searchType;  //  username, title, content, title_content
    private String searchWord;
}
