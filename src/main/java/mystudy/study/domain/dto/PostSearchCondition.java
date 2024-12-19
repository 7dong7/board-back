package mystudy.study.domain.dto;

import lombok.Data;

@Data
public class PostSearchCondition {

    private String searchType;  //  username, title, content, title_content
    private String searchWord;
}
