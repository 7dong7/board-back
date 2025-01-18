package mystudy.study.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class MemberRegisterForm {

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String username;

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Range(min = 4,max = 10, message = "비밀번호는 4글자 이상 10글자 이하로 작성해주세요.")
    private String password;

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Range(min = 4,max = 10, message = "비밀번호는 4글자 이상 10글자 이하로 작성해주세요.")
    private String confirmPassword;

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String email;

}
