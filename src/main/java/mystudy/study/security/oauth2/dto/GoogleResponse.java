package mystudy.study.security.oauth2.dto;


import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private Map<String, Object> attribute;

    /**
     *  {
     *      sub=101963555048079144128,
     *      name=박동빈,
     *      given_name=동빈,
     *      family_name=박,
     *      picture=https://lh3.googleusercontent.com/a/ACg8ocI8tMspxsKrAVG54rRPyvWAgxb065yPIbe_tiInCQQ42WLu1A=s96-c,
     *      email=spino0514@gmail.com,
     *      email_verified=true
     *  }
     */
    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getNickname() {
        return attribute.get("name").toString();
    }

    @Override
    public String getMobile() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public String getLoginId() {
        return getProvider()+"_"+getProviderId();
    }
}
