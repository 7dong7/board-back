package mystudy.study.domain.member.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginForm {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
