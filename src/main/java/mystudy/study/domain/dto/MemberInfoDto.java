package mystudy.study.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberInfoDto {

    // member 엔티티
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    // post 엔티티
    private Long postCount; // 게시글 수

    // comment 엔티티
    private Long commentCount; // 댓글 수

}
