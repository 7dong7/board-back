package mystudy.study.repository.member;

import mystudy.study.domain.dto.member.MemberSearchCondition;
import mystudy.study.domain.dto.member.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> searchMember(MemberSearchCondition condition);

    // 사용자 페이징, 조건 검색, 정렬
    Page<SearchMemberDto> getMemberPage(MemberSearchCondition condition, Pageable pageable);

    // 사용자 로그인
    Member login(String loginId, String password);
}
