package mystudy.study.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.entity.MemberStatus;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.member.entity.Member;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberQueryService memberQueryService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member loginMember = memberQueryService.findByEmail(email);
        log.info("loadUserByUsername loginMember [username,email: {}, name: {}, nickname: {}]", loginMember.getEmail(), loginMember.getName(), loginMember.getNickname());

        if (loginMember.getStatus() == MemberStatus.DELETE) {
            log.info("탈퇴한 회원");
            throw new UsernameNotFoundException("탈퇴한 회원입니다.");
        }
        
        return new CustomUserDetail(loginMember);
    }
}