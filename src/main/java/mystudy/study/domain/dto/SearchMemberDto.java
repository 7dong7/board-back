package mystudy.study.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SearchMemberDto {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    @QueryProjection
    public SearchMemberDto(Long id, String username, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }
}
