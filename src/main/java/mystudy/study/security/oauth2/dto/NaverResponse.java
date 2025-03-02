package mystudy.study.security.oauth2.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

    /**
     *  {
     *      resultcode=00,
     *      message=success,
     *      response={
     *          id=네이버에서 사용하는 사용자 식별자,
     *          nickname=박동빈,
     *          age=20-29,
     *          gender=M,
     *          email=tmdkdl777@naver.com,
     *          mobile=010-4224-0189,
     *          mobile_e164=+821042240189,
     *          name=박동빈,
     *          birthday=03-25,
     *          birthyear=1997
     *          }
     *  }
     *
     */
    public NaverResponse(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getNickname() {
        return attributes.get("nickname").toString();
    }

    @Override
    public String getMobile() {
        return attributes.get("mobile").toString();
    }

    @Override
    public String getGender() {
        String gender = attributes.get("gender").toString(); // M F
        return switch (gender) {
            case "M" -> "남";
            case "F" -> "여";
            default -> null;
        };
    }

    @Override
    public String getLoginId() {
        return getProvider()+"_"+getProviderId();
    }
}
