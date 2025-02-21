package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.login.MemberLoginForm;
import mystudy.study.domain.member.service.login.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // 로그인 : 페이지
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")MemberLoginForm loginForm) {
        return "pages/login/login";
    }

}
