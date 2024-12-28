package mystudy.study.domain.dto.comment;

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
    private List<CommentDto> replies = new ArrayList<>(); // 댓글 대댓글
}
