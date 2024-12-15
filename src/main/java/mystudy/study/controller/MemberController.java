package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.dto.SearchMemberDto;
import mystudy.study.domain.entity.Member;
import mystudy.study.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.util.StringUtils.hasText;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 사용자 검색
    @GetMapping("/members/search")
    public String searchMember(
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String email,
            Model model,
            Pageable clPageable) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                clPageable.getPageNumber(),
                Math.max(1, Math.min(clPageable.getPageSize(), 50)), // 1 이상, 50 이하로 페이지 크기 제한
                clPageable.getSort().isSorted() ? clPageable.getSort() : Sort.by("id").descending() // 정렬 조건 처리
        );


//        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println("pageable.getPageNumber() = " + pageable.getPageNumber());
//        System.out.println("pageable.getPageSize() = " + pageable.getPageSize());
//        System.out.println("pageable.getOffset() = " + pageable.getOffset());
//        System.out.println("pageable.getSort() = " + pageable.getSort());

        // 사용자 검색 조건
        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setUsername(username);
        condition.setEmail(email);

        // 사용자 검색
        Page<SearchMemberDto> memberList = memberService.searchMembers(condition, pageable);

//        for (SearchMemberDto memberDto : memberList) {
//            System.out.println("memberDto = " + memberDto);
//        }

        // model 속성 추가
        model.addAttribute("memberList", memberList);

        return "member/members";
    }

}
