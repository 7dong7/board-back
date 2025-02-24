package mystudy.study.advice.dto;

import lombok.Data;
import mystudy.study.security.CustomUserDetail;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class CurrentMemberDto {

    private Long memberId; // 회원 번호
    private String name;    // 회원 이름
    private String nickname;    // 닉네임

    public CurrentMemberDto(CustomUserDetail userDetails) {
        this.memberId = userDetails.getMemberId();
        this.name = userDetails.getName();
        this.nickname = userDetails.getNickname();
    }
}
