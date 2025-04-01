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

    /**
     *  OAuth2 방식으로 로그인하는 경우 
     *  access 토큰을 쿠키로 발급해야된다 ( 요청에 대한 응답을 하는게 아니라 href 방식으로 이동하기 때문에 )
     *      프론트에서는 쿠키에 접근할 수 없음 (서버에서 HttpOnly 설정,  카카오에서는 SDK 사용 권장 )
     *  프론트에서 발급받은 access 토큰을 가지고 "/api/OAuth2/handler" 에 요청을 하면
     *  쿠키에 담긴 access 토큰을 response header 담아서 프론트에 전달하고
     *  쿠키에 담긴 access 토큰을 삭제한다
     */
    @GetMapping("/api/OAuth2/handler")
    public ResponseEntity<String> OAuth2Handler(HttpServletRequest request, HttpServletResponse response) {
        log.info("OAuth2Handler localStorage 에 담기");

        // 쿠키에서 access 토큰값 추출
        String access = Arrays.stream(request.getCookies())
                .filter(cookie -> "access".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
        log.info("cookies info access: {}", access);

        response.addCookie(deleteCookie("access"));

        response.setHeader("access", access);
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
