package mystudy.study.domain.member.dto;

import lombok.Data;

@Data
public class PasswordForm {

    private String currentPassword; // 현재 사용중인 비밀번호

    private String newPassword; // 새로운 비밀번호
    private String confirmPassword; // 새로운 비밀번호 다시 입력 (확인)
}
