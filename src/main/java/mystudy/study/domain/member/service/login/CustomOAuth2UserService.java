package mystudy.study.domain.member.service.login;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.login.CustomOAuth2User;
import mystudy.study.domain.member.dto.login.GoogleResponse;
import mystudy.study.domain.member.dto.login.NaverResponse;
import mystudy.study.domain.member.dto.login.OAuth2Response;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.entity.RoleType;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.member.service.MemberService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberQueryService memberQueryService;
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 리소스 서버에 유저 정보 요청 (토큰으로)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("CustomOAuth2UserService loadUser oAuth2User: {}", oAuth2User.getAttributes());

        // 외부 서버 이름
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) { // 네이버 로그인의 경우
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        }
        else if (registrationId.equals("google")) { // 구글 로그인의 경우
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        }
//        else if (registrationId.equals("kakao")) {
//
//        }
        else {
            return null;
        }

        // DB에 저장, 조회할 username
            // 실제 이메일은 아니고 저장, 조회용으로 사용
        String email = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // 기존에 있는 사용자 조회
        Member findMember = memberQueryService.findByEmail(email);
        RoleType role = RoleType.USER;

        if( findMember == null ) { // 사용자가 처음인 경우

            Member newMember = Member.builder()
                    .email(email)
                    .name(oAuth2Response.getName())
                    .role(role)
                    .build();

            memberService.saveMember(newMember); // 사용자 저장
        }
        else { // 기존 사용자인 경우

            role = findMember.getRole();
        }

        return new CustomOAuth2User(oAuth2Response, role.name());
    }
}
