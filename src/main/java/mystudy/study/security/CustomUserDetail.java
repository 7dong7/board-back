package mystudy.study.security;

import mystudy.study.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetail implements UserDetails {

    private final Member member;

    public CustomUserDetail(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole().name();
            }
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    } // 비밀번호

    @Override
    public String getUsername() { // 아이디로 사용하는 email 반환
        return member.getEmail();
    } // username, email

    public String getName() { // 회원 본명
        return member.getName();
    } // 이름 (본명)

    public String getNickname() { // 회원가 지정한 닉네임
        return member.getNickname();
    } // 닉네임 (별명)

    public Long getMemberId() {
        return member.getId();
    } // 사용자 번호, memberId
    
    
}
