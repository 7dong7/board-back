package mystudy.study.domain.comment.dto;

import lombok.Data;

@Data
public class WriteCommentForm {

    private String content;
    private Long parentId;
}
