package mystudy.study.domain.member.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginForm {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
