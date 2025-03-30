package mystudy.study.domain.member.dto.search;

import lombok.Data;

@Data
public class MemberDetailSearchCondition {
    // post condition
    private int postPageNumber = 1; // 기본 페이지 번호
    private String postSort = "id"; // 기본 정렬 조건
    private String postDirection = "DESC"; // 기본 정렬 방향
    // comment condition
    private int commentPageNumber = 1; 
    private String commentSort = "id";
    private String commentDirection = "DESC";
}
