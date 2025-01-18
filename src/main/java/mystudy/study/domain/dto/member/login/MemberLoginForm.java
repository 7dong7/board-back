package mystudy.study.domain.dto.member.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginForm {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
