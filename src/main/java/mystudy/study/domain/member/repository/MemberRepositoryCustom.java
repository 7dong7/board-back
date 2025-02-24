package mystudy.study.domain.member.repository;

import mystudy.study.domain.member.dto.search.SearchMemberInfoDto;
import mystudy.study.domain.member.dto.EditMemberDto;
import mystudy.study.domain.member.dto.MemberSearch;
import mystudy.study.domain.member.dto.search.MemberSearchCondition;
import mystudy.study.domain.member.dto.SearchMemberDto;
import mystudy.study.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    List<Member> searchMember(MemberSearchCondition condition);
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

    // 회원 페이징, 조건 검색, 정렬  // ============= 삭제 예정 ===============//
    Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable);

    // 회원 로그인 // =============== 삭제 예정 ==================
    Member login(String loginId, String password);



}
