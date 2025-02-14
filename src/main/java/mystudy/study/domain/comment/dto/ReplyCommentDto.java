package mystudy.study.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
public class ReplyCommentDto {

    private Long commentId;           // 대댓글 ID
    private String content;           // 대댓글 내용
    private String author;            // 대댓글 작성자
    private LocalDateTime createdAt;  // 작성일
    private Long parentId;            // 부모 댓글 ID

    @QueryProjection
    public ReplyCommentDto(Long commentId, String content, String author, LocalDateTime createdAt, Long parentId) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.parentId = parentId;
    }
}
