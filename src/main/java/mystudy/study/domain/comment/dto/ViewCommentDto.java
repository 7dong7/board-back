package mystudy.study.domain.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import mystudy.study.domain.comment.entity.CommentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ViewCommentDto {

    private Long commentId;             // 댓글 Id
    private String content;             // 댓글 내용
    private String author;              // 댓글 작성자
    private LocalDateTime createdAt;    // 작성일
    private CommentStatus status;       // 삭제 여부

    private Long memberId;          // 본인 확인 여부

    private List<ViewReplyDto> replies = new ArrayList<>();  // 대댓글 (댓글에 대한 댓글)

    @QueryProjection
    public ViewCommentDto(Long commentId, String content, String author, LocalDateTime createdAt, CommentStatus status, Long memberId) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.status = status;
        this.memberId = memberId;
    }
}