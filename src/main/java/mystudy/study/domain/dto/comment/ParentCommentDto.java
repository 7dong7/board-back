package mystudy.study.domain.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class ParentCommentDto {

    private Long commentId;             // 댓글 Id
    private String content;             // 댓글 내용
    private String author;              // 댓글 작성자
    private LocalDateTime createdAt;    // 작성일

    private List<ReplyCommentDto> replies = new ArrayList<>();  // 대댓글 (댓글에 대한 댓글)

    @QueryProjection
    public ParentCommentDto(Long commentId, String content, String author, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }
}