package mystudy.study.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class PostSearchCondition {

    private String searchType;  //  username, title, content, title_content
    private String searchWord;
}
