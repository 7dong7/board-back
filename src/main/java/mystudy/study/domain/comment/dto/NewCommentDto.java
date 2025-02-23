package mystudy.study.domain.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class NewCommentDto { // ======== 삭제 예정 ========= //

    private Long postId;    // 게시글 id
    private String content; // 대글 내용
    private Long parentId;  // 대댓글인 경우 부모 댓글 id

    @Builder
    public NewCommentDto(Long postId, String content, Long parentId) {
        this.postId = postId;
        this.content = content;
        this.parentId = parentId;
    }
}
