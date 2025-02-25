package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    // 로그인 : 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "pages/login/login";
    }

}
