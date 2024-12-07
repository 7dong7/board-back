package mystudy.study.repository;

import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> searchMember(MemberSearchCondition condition);
}
