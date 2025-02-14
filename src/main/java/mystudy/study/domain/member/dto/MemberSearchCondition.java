package mystudy.study.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSearchCondition {

    private String searchType; // 검색 조건 username, email
    private String searchWord; // 검색어
}
