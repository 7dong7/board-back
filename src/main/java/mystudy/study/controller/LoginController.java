package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.member.dto.login.LoginSessionInfo;
import mystudy.study.domain.member.dto.login.MemberLoginForm;
import mystudy.study.domain.member.entity.Member;
import mystudy.study.domain.member.service.login.LoginService;
import mystudy.study.session.SessionConst;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
