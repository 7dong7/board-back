package mystudy.study.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentViewDto {

    private Long commentId; // 댓글 id
    private String content; // 댓글 내용
    private String author; // 댓글 작성자
    private LocalDateTime createdAt; // 댓글 작성일
    private Long parentId; // 부모 댓글 id (대댓글인 경우 값이 있음)
    private List<CommentViewDto> replies = new ArrayList<>(); // 댓글 대댓글

    @QueryProjection
    public CommentViewDto(Long commentId, String content, String author, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public CommentViewDto(Long commentId, String content, String author, LocalDateTime createdAt, Long parentId) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.parentId = parentId;
    }
}
