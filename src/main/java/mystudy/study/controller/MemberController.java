package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.service.MemberService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

}
