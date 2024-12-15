package mystudy.study.controller;

import lombok.RequiredArgsConstructor;
import mystudy.study.domain.dto.MemberSearchCondition;
import mystudy.study.domain.entity.Member;
import mystudy.study.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    // 사용자 검색
    @GetMapping("/members/search")
    public String searchMember(
            @RequestParam(required = false, defaultValue = "") String keyword,
            Model model,
            Pageable clPageable) {

        // pageable 생성
        Pageable pageable = PageRequest.of(
                clPageable.getPageNumber(),
                clPageable.getPageSize() > 0 && clPageable.getPageSize() < 50 ? clPageable.getPageSize() : 20,
                clPageable.getSort().isSorted() ? clPageable.getSort() : Sort.by("member_id").descending()
        );

        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("pageable.getPageNumber() = " + pageable.getPageNumber());
        System.out.println("pageable.getPageSize() = " + pageable.getPageSize());
        System.out.println("pageable.getOffset() = " + pageable.getOffset());
        System.out.println("pageable.getSort() = " + pageable.getSort());

//
//        MemberSearchCondition condition = new MemberSearchCondition();
//
//        memberService.searchMembers(condition, pageable);

        return "member/members";
    }

}
