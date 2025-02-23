package mystudy.study.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import mystudy.study.domain.comment.entity.CommentStatus;

import java.time.LocalDateTime;

@Data
public class ViewReplyDto {

    private Long commentId;           // 대댓글 ID
    private Long parentId;            // 부모 댓글 ID
    private String content;           // 대댓글 내용
    private String author;            // 대댓글 작성자
    private LocalDateTime createdAt;  // 작성일
    private CommentStatus status; // 삭제 여부

    private Long memberId;          // 본인 확인 여부

    @QueryProjection
    public ViewReplyDto(Long commentId, Long parentId, String content, String author, LocalDateTime createdAt, CommentStatus status, Long memberId) {
        this.commentId = commentId;
        this.parentId = parentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.status = status;
        this.memberId = memberId;
    }
}