package mystudy.study.security.oauth2.dto;

import java.util.HashMap;
import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private Map<String, Object> attribute;

    /**
     *  {
     *      id=3944194244,
     *      connected_at=2025-03-02T06:50:58Z,
     *      properties={
     *          nickname=레모니스
     *          },
     *      kakao_account={
     *          profile_nickname_needs_agreement=false,
     *          profile_image_needs_agreement=true,
     *          profile={
     *              nickname=레모니스,
     *              is_default_nickname=false
     *              },
     *          has_email=true,
     *          email_needs_agreement=false,
     *          is_email_valid=true,
     *          is_email_verified=true,
     *          email=remonice@kakao.com
     *          }
     *   }
     */
    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    // 사용자 식별
    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }


    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");

        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return profile.get("nickname").toString();
    }

    @Override
    public String getMobile() {
        return "";
    }

    @Override
    public String getGender() {
        return "";
    }

    @Override
    public String getLoginId() {
        return getProvider()+"_"+getProviderId();
    }
}
