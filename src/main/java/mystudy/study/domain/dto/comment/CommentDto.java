package mystudy.study.domain.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
}
