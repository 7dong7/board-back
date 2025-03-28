package mystudy.study.domain.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EditPostApiDto {
    private String title; // 변경 제목
    private String content; // 변경 내용
}
