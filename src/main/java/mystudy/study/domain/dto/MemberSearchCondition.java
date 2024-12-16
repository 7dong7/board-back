package mystudy.study.domain.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {

    private String searchType; // 검색 조건 username, email
    private String searchWord; // 검색어
}
