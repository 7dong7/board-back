package mystudy.study.domain.member.dto.login;

import lombok.Data;

import java.util.Map;

@Data
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attributes;

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


}
