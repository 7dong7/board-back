package mystudy.study.domain.post.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class NewPostDto {
    
    private String title; // 게시글 제목
    private String content; // 게시글 내용
}
