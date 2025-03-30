package mystudy.study.domain.member.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetMemberDetail { // api 요청에서 사용

    // member
    private Long memberId;      // 회원 번호
    private String nickname;    // 닉네임
    private String email;       // 이메일 (아이디) (OAuth2의 경우 아이디가 아님)
    private LocalDateTime createdAt; // 계정 생성일
    private String name;        // 이름

    // post
    private Long postCount;     // 작성한 게시글 수

    // comment
    private Long commentCount;  // 작성한 댓글 수
}
