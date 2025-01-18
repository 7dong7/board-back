package mystudy.study.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mystudy.study.domain.dto.member.login.MemberLoginForm;
import mystudy.study.domain.entity.Member;
import mystudy.study.service.login.LoginService;
import mystudy.study.session.SessionConst;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")MemberLoginForm loginForm) {
        return "pages/login/login";
    }

    // 로그인
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") MemberLoginForm loginForm, BindingResult bindingResult,
                        HttpServletRequest request) {
        log.info("loginForm: {}", loginForm);

        if (bindingResult.hasErrors()) {
            return "pages/login/login";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인 실패 처리    아이디, 비번 DB 불일치
        if (loginMember == null) {
            bindingResult.reject("notFoundMember", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "pages/login/login";
        }

        // 로그인 성공 처리
            // 세션 생성
        HttpSession session = request.getSession();
            // 로그인 회원 정보 저장
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, loginMember.getId());

        return "redirect:/members";
    }


}
