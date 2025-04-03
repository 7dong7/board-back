package mystudy.study.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import mystudy.study.domain.member.entity.MemberStatus;
import mystudy.study.validation.PasswordMatch;
import org.hibernate.validator.constraints.Length;

@Data
@PasswordMatch(passwordField = "password", confirmPasswordField = "confirmPassword")
public class MemberProfile {
    /**
     * 사용자의 정보를 수정할 수 있는 정보를 보여주는 DTO
     * 수정 가능한 정보를 담아서 보여주면 된다
     */
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 2, max = 12,
            message = "닉네임은 2글자 이상 20글자 이하로 작성해주세요.")
    private String nickname;

    @Pattern(regexp = "^(010|011)-\\d{4}-\\d{4}$",
            message = "전화번호의 형식을 다시 확인해주세요.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String mobile;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
            message = "영문+숫자 조합이어야 합니다.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;
    @NotBlank(message = "비밀번호 확인을 필수 입니다.")
    private String confirmPassword;

    private MemberStatus status;
}
