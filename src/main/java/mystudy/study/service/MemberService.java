package mystudy.study.service;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    @Transactional
    public Page<SearchMemberDto> searchMembers(MemberSearchCondition condition, Pageable pageable) {
        return memberRepository.searchMembers(condition, pageable);
    }
}
