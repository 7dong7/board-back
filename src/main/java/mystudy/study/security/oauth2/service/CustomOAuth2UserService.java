package mystudy.study.security.oauth2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.entity.RoleType;
import mystudy.study.domain.member.repository.MemberRepository;
import mystudy.study.domain.member.service.MemberQueryService;
import mystudy.study.domain.member.service.MemberService;
import mystudy.study.security.oauth2.dto.GoogleResponse;
import mystudy.study.security.oauth2.dto.KakaoResponse;
import mystudy.study.security.oauth2.dto.NaverResponse;
import mystudy.study.security.oauth2.dto.OAuth2Response;
import mystudy.study.security.oauth2.user.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberQueryService memberQueryService;
    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 리소스 서버에 유저 정보 요청 (토큰으로)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 리소스 서버에서 받아온 정보
        log.info("CustomOAuth2UserService loadUser oAuth2User: {}", oAuth2User.getAttributes());

        /*현재 naver 에서 받아온 데이터 중 age, birthday, birthyear 은 아직 처리하지 않음
        * 현재 google name 과 email 만 처리했다
        * */
        
        
        // 외부 서버 이름
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) { // 네이버 로그인의 경우
            log.info("naver 로그인");
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        }
        else if (registrationId.equals("google")) { // 구글 로그인의 경우
            log.info("google 로그인");
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        }
        else if (registrationId.equals("kakao")) {
            log.info("kakao 로그인");
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        // 로그인 회원가 기존의 회원인지 조회
        String loginId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Member findMember = memberQueryService.findByLoginId(loginId);

//        Member findMember = memberQueryService.findByProviderId(oAuth2Response.getProviderId());
        RoleType role = RoleType.ROLE_USER;

        if( findMember == null ) { // 회원이 처음인 경우

            Member newMember = Member.builder()
                    .loginId(loginId)
                    .provider(oAuth2Response.getProvider())
                    .providerId(oAuth2Response.getProviderId())
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .nickname(oAuth2Response.getNickname())
                    .gender(oAuth2Response.getGender())
                    .mobile(oAuth2Response.getMobile())
                    .role(RoleType.ROLE_USER)
                    .build();

            memberService.saveMember(newMember);
        }
        else { // 기존 회원인 경우 -> DB에 등록된 회원 정보를 외부서버의 최신 정보로 업데이트

            findMember.updateNickname(oAuth2Response.getNickname());
            findMember.updateMobile(oAuth2Response.getMobile());

            role = findMember.getRole();
        }

        findMember = memberQueryService.findByLoginId(loginId);

        return new CustomOAuth2User(oAuth2Response, role.name(), findMember.getId());
    }
}