package mystudy.study.api.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@Slf4j
public class OAuth2ApiController {


    @GetMapping("/api/OAuth2/handler")
    public ResponseEntity<String> OAuth2Handler(HttpServletRequest request, HttpServletResponse response) {
        log.info("OAuth2Handler localStorage에 담기");

        // 쿠키에서 access 토큰값 추출
        String access = Arrays.stream(request.getCookies())
                .filter(cookie -> "access".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        String username = Arrays.stream(request.getCookies())
                .filter(cookie -> "username".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        log.info("cookies info [access: {}, username: {}]", access, username);

        response.addCookie(deleteCookie("access"));
        response.addCookie(deleteCookie("username"));

        response.setHeader("access", access);
        response.setHeader("username", username);

        return new ResponseEntity<>("응답 성공", HttpStatus.OK);
    }

    // == 기존 쿠키 삭제 ==
    private Cookie deleteCookie(String name) {

        Cookie cookie = new Cookie(name, null); // 값은 null로 설정
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 동일한 경로 지정
        cookie.setHttpOnly(true); // 기존 쿠키와 동일한 속성 유지

        return cookie;
    }
}
