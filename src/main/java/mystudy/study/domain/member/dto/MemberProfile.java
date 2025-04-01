package mystudy.study.domain.member.dto;

import lombok.Data;
import mystudy.study.domain.member.entity.MemberStatus;

@Data
public class MemberProfile {
    /**
     * 사용자의 정보를 수정할 수 있는 정보를 보여주는 DTO
     * 수정 가능한 정보를 담아서 보여주면 된다
     */
    private String password;
    private String nickname;
    private String mobile;
    private MemberStatus status;
}
