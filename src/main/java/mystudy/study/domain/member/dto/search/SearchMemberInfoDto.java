package mystudy.study.domain.member.dto.search;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import mystudy.study.domain.member.entity.MemberStatus;

import java.time.LocalDateTime;

@Data
public class SearchMemberInfoDto {

    private Long memberId;              // 회원번호
    private String email;               // 이메일, 아이디
    private String nickname;            // 닉네임
    private String name;                // 이름
    private LocalDateTime createdAt;    // 생성일
    private MemberStatus status;        // 탈퇴 여부

    @QueryProjection
    public SearchMemberInfoDto(Long memberId, String email, String nickname, String name, LocalDateTime createdAt, MemberStatus status) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.createdAt = createdAt;
        this.status = status;
    }
}
