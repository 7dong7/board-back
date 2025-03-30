package mystudy.study.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

// ========= 비밀키 =========
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

// ========= 토큰 생성 =========
    public String createJWT(String category, Long memberId, String username, String role, String nickname, Long expiredMs) {
        // category -> "refresh", "access" 토큰 종류
        // username -> 회원이름 // email
        // nickname -> 닉네임
        // role -> 권한
        // expiredMs -> 만료시간

        // JWT builder 값 설정하기
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("id", memberId)
                .claim("nickname", nickname)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 발급일
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
        // 1초 = 1,000 밀리초
        // 1분 = 60 * 1,000 밀리초 = 60,000 밀리초
        // 1시간 = 60 * 60 * 1,000 밀리초 = 3,600,000 밀리초
    }

    
// ========= 토큰 검증 =========
    // claims 추출 (토큰을 파싱하는 것만으로도 토크의 유효성을 체크할 수 있다)
    public Claims validClaims(String token) throws ExpiredJwtException, SignatureException, MalformedJwtException, IllegalArgumentException {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    // 회원 번호 추출
    public Long getMemberId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("id", Long.class);
    }
    
    // 토큰 회원 이름 추출 & 이메일 추출
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }
    
    // 토큰 닉네임 추출
    public String getNickname(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }
    
    // 토큰 권한 추출
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    
    // 토큰 카테고리 추출
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // 토큰 만료일 검증 (만료:true, 유효:false)
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                // 만료일을 추출. 현재일 보다 이전이라면 만료일이 지났다 -> return true
                //    만료일이 현재일 보다 이후라면 아직 만료되지 않았다 -> return false
                .getExpiration().before(new Date());
    }
}
