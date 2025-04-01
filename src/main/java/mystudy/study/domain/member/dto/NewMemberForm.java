package mystudy.study.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Data
public class NewMemberForm {

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
        // 생년월일의 앞 4자리(연도)를 정수로 변환
        int year = Integer.parseInt(frontNum.substring(0, 4));

        if (year < 2000) { // 1900~1999년생
            if ("1".equals(backNum)) {
                return "남";
            } else if ("2".equals(backNum)) {
                return "여";
            } else {
                throw new IllegalArgumentException("주민번호 뒷자리를 다시 확인해 주세요.");
            }
        } else { // 2000년 이후 생
            if ("3".equals(backNum)) {
                return "남";
            } else if ("4".equals(backNum)) {
                return "여";
            } else {
                throw new IllegalArgumentException("주민번호 뒷자리를 다시 확인해 주세요.");
            }
        }
    }

}
