package mystudy.study.domain.dto.post;

import lombok.*;

@Data
@NoArgsConstructor
public class NewPostDto {
    
    private String title; // 게시글 제목
    private String content; // 게시글 내용
}
