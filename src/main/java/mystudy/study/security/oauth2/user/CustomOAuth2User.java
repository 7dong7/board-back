package mystudy.study.security.oauth2.user;

import lombok.Getter;
import mystudy.study.security.oauth2.dto.OAuth2Response;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;
    private final String role;

    @Getter
    private final Long memberId;

    public CustomOAuth2User(OAuth2Response oAuth2Response, String role, Long memberId) {
        this.oAuth2Response = oAuth2Response;
        this.role = role;
        this.memberId = memberId;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });

        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }

    public String getEmail() {
        return oAuth2Response.getEmail();
    }

    public String getNickname() {
        return oAuth2Response.getNickname();
    }

    public String getLoginId() {
        return oAuth2Response.getLoginId();
    }

}