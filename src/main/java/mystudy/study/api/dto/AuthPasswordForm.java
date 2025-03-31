package mystudy.study.api.dto;

import lombok.Data;

@Data
public class AuthPasswordForm {

    private Long memberId;
    private String password;
}
