package mystudy.study.domain.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCommentForm {

    private Long postId; // 게시글 번호
    private String comment; // 작성 댓글 내용
    private Long parentId; // 댓글의 부모 댓글 번호 (대댓글의 경우)
}
