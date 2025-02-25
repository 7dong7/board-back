package mystudy.study.domain.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Data
public class RegisterMemberForm {

    @Pattern(regexp = "^[^\\s]+$",
            message = "공백 문자를 포함할 수 없습니다.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String email;       // 아이디

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 4,max = 12, message = "비밀번호는 4글자 이상 12글자 이하로 작성해주세요.")
    private String password;    // 비밀번호
    
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String confirmPassword; // 비밀번호 재입력

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 4,max = 20,
            message = "이름은 4글자 이상 20글자 이하로 작성해주세요.")
    private String name;        // 회원 본명

    @NotBlank(message = "공백은 사용할 수 없습니다.")
    @Length(min = 4,max = 20,
            message = "닉네임은 4글자 이상 20글자 이하로 작성해주세요.")
    private String nickname;    // 닉네임

    @Pattern(regexp = "^(010|011)-\\d{4}-\\d{4}$",
            message = "전화번호의 형식을 다시 확인해주세요.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String mobile;      // 번호

    // 주민번호 19970325-1 생년월일 과 주민번호 앞자리 하나
    @Pattern(regexp = "[0-9]{8}",
            message = "해당값은 사용할 수 없습니다.")
    @DateTimeFormat(pattern = "yyyyMMdd")
    private String frontNum; // 19970325 -> 나이, 생일 구별

    @Pattern(regexp = "[1-4]{1}",
            message = "해당값은 사용할 수 없습니다.")
    @NotBlank(message = "공백은 사용할 수 없습니다.")
    private String backNum; // 1 -> 성별 구별용


    // == 메소드 == //

    // 생년월일 반환
    public String getResidentNumber() {
        return frontNum +"-"+backNum;
    }

    // 나이 반환
    public String getAge() {
        // 회원 생년월일 19970325
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthday = LocalDate.parse(frontNum, formatter);
        
        // 오늘
        LocalDate now = LocalDate.now();
        
        // 생일과 오늘 비교 (생일이 지나지 않으면 나이가 하나 낮아짐)
        int age = Period.between(birthday, now).getYears();

        return String.valueOf(age);
    }

    // 생일 반환
    public String getBirthday() {
        // 회원 생년월일 19970325
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthday = LocalDate.parse(frontNum, formatter);

        // 생일만 추출 ( MM월 dd일 )
        DateTimeFormatter birthDayFormatter = DateTimeFormatter.ofPattern("MM월 dd일");

        return birthday.format(birthDayFormatter);
    }

    // 성별 반환
    public String getGender() {
        if (backNum == null) {
            return "알 수 없음";
        }
        switch (backNum) {
            case "1":
            case "3":
                return "남";
            case "2":
            case "4":
                return "여";
            default:
                return "알 수 없음";
        }
    }
}
