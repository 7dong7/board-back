package mystudy.study.domain.member.dto.login;

public interface OAuth2Response {

    // 제공자에서 발급해주는 아이디 (번호)
    // 동일인 식별 정보 -> 애플리케이션당 유니크한 일련번호값
    String getProviderId();

    String getProvider();   // 제공자 이름 (naver, google)

    String getEmail();      // email

    String getName();       // 사용자 이름

    String getNickname();   // 별명

    String getMobile();

    String getGender();
}
