package mystudy.study.advice.dto;

import lombok.Data;
import mystudy.study.security.CustomUserDetail;
import mystudy.study.security.oauth2.user.CustomOAuth2User;
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

    public CurrentMemberDto(CustomOAuth2User customOAuth2User) {
        this.memberId = customOAuth2User.getMemberId();
        this.name = customOAuth2User.getName();
        this.nickname = customOAuth2User.getNickname();
    }
}
