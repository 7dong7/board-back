package mystudy.study.domain.member.repository;

import mystudy.study.domain.member.dto.search.SearchMemberInfoDto;
import mystudy.study.domain.member.dto.EditMemberDto;
import mystudy.study.domain.member.dto.MemberSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberRepositoryCustom {

    /**
     * @param searchType 검색 조건
     * @param memberSearch memberSearch.getSearchWord() 검색어
     * @param pageable 생성된 pageable 객체
     * @return 페이징 객체 반환
     */
    // 회원 검색 조건에 맞는 회원 조회 (페이징)
    Page<SearchMemberInfoDto> getSearchMemberPage(String searchType, MemberSearch memberSearch, Pageable pageable);

    // 회원 정보 수정 (본인만) - 회원 정보 조회
    Optional<EditMemberDto> getEditMember(Long memberId);
}
