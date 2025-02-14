package mystudy.study.domain.member.dto.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginSessionInfo {

    private Long id; // 사용자 id
    private String username;
}
