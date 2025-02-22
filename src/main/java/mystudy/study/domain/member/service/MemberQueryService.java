package mystudy.study.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.EditMemberDto;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.repository.MemberRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberQueryService {

    private final MemberRepository memberRepository;

    // id로 사용자 조회
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다. memberId: "+memberId));
    }
    
    // email 로 사용자 조회
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // OAuth2 로그인 사용자 조회
    public Member findByProviderId(String providerId) {
        return memberRepository.findByProviderId(providerId);
    }

    // 사용자 정보 수정 (본인만) - 사용자 정보 조회
    public EditMemberDto getEditMemberDto(Long memberId) {
        /**
         * 잘못된 요청에 대한 처리
         * 1. 존재하지 않는 사용자에 대한 정보를 요청하는 경우
         * 2. 다른 사용자의 정보를 요청하는 경우
         */
        // 응답 정보 조회 -> 없는 경우 예외 처리
        EditMemberDto editMemberDto = memberRepository.getEditMember(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다. memberId: " + memberId));

        // 현재 로그인한 사용자의 email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 로그인한 사용자가 다른 사람의 정보를 요청하는 경우
        if (!editMemberDto.getEmail().equals(email)) { // 같지 않은 경우
            throw new IllegalArgumentException("잘못된 요청입니다. 사용자: "+email+ "가 다른 사용자: "+editMemberDto.getEmail()+"의 정보를 수정하려고 접근했습니다.");
        }
        
        // 요청정보 반환
        return editMemberDto;
    }
}
