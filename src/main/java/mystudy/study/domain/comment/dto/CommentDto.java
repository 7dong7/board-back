package mystudy.study.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long commentId;
    private String content;
    private String author; // username
    private LocalDateTime createdAt;
    private Long postId;

    @QueryProjection
    public CommentDto(Long commentId, String content, String author, LocalDateTime createdAt, Long postId) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.postId = postId;
    }
}
