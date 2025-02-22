package mystudy.study.domain.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterMemberForm {

    @Pattern(regexp = "^[^\\s]+$",
            message = "공백 문자를 포함할 수 없습니다.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String email;       // 아이디

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 4,max = 10, message = "비밀번호는 4글자 이상 10글자 이하로 작성해주세요.")
    private String password;    // 비밀번호
    
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String confirmPassword; // 비밀번호 재입력

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String name;        // 회원 본명

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String nickname;    // 닉네임

    // @NotBlank 은 text, String 타입을 검증하는데 사용한다
    @Positive
    @Min(0)
    private Integer age;            // 나이대

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String mobile;      // 번호

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String gender;      // 성볋 (남,여)

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String birthday;    // 생일

}
