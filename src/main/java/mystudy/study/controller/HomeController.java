package mystudy.study.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    // 홈으로 들어오면 게시글 목록으로
    @GetMapping("/")
    public String home() {
        return "redirect:/posts";
    }
}
