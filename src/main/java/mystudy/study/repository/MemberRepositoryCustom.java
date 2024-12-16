package mystudy.study.repository;

import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> searchMember(MemberSearchCondition condition);

    // 사용자 페이징, 조건 검색, 정렬
    Page<SearchMemberDto> searchMembers(MemberSearchCondition condition, Pageable pageable);
}
