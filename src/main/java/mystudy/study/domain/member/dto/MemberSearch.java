package mystudy.study.domain.member.dto;

import lombok.Data;
import mystudy.study.domain.member.dto.search.MemberSearchType;
import mystudy.study.domain.member.dto.search.SearchMemberInfoDto;
import org.springframework.data.domain.Page;

@Data
public class MemberSearch {

    // 검색 타입 리스트 ( view 표시 )
    private MemberSearchType[] searchTypes = MemberSearchType.values();
    // 회원 검색 결과 페이지 ( view 표시 )
    private Page<SearchMemberInfoDto> searchMembers;

    // 검색 타입 ( view 표시 & 파라미터 )
    private String searchType = "id";
    // 검색어
    private String searchWord;
    

    // == 메소드 ==
    public void addSearchMembers(Page<SearchMemberInfoDto> searchMembers) {
        this.searchMembers = searchMembers;
    }

}
