package mystudy.study.domain.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EditMemberDto {

    private Long memberId;          // 회원 번호
    private String email;           // 이메일, 아이디
    private LocalDateTime createdAt; // 계정 생성일
    
    private String nickname;        // 닉네임
    private String mobile;          // 휴대폰 번호

    @QueryProjection
    public EditMemberDto(Long memberId, String email, LocalDateTime createdAt, String nickname, String mobile) {
        this.memberId = memberId;
        this.email = email;
        this.createdAt = createdAt;
        this.nickname = nickname;
        this.mobile = mobile;
    }
}
