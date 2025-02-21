package mystudy.study.domain.member.repository;

import mystudy.study.domain.member.dto.MemberSearchCondition;
import mystudy.study.domain.member.dto.SearchMemberDto;
import mystudy.study.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    List<Member> searchMember(MemberSearchCondition condition);

    // 사용자 페이징, 조건 검색, 정렬
    Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable);

    // 사용자 로그인 // =============== 삭제 예정 ==================
    Member login(String loginId, String password);
}
