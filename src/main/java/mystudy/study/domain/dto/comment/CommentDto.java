package mystudy.study.domain.dto.comment;

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

    @QueryProjection
    public CommentDto(Long commentId, String content, String author, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }
}
